/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Bo-Xuan Fan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bxf.hradmin.testgen.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.bxf.hradmin.common.exception.ModuleInfo;
import com.bxf.hradmin.testgen.dto.GenerateCond;
import com.bxf.hradmin.testgen.dto.QuestionFile;
import com.bxf.hradmin.testgen.model.AnswerSnapshot;
import com.bxf.hradmin.testgen.model.QuestLevel;
import com.bxf.hradmin.testgen.model.Question;
import com.bxf.hradmin.testgen.model.QuestionSnapshot;
import com.bxf.hradmin.testgen.model.Version;
import com.bxf.hradmin.testgen.repository.QuestLevelRepository;
import com.bxf.hradmin.testgen.repository.QuestionRepository;
import com.bxf.hradmin.testgen.repository.VersionRepository;
import com.bxf.hradmin.testgen.service.TestGenException;
import com.bxf.hradmin.testgen.service.TestGenerationService;
import com.bxf.hradmin.testgen.service.TestGenerator;
import com.bxf.hradmin.utils.BeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * TestGenerationServiceImpl
 *
 * @since 2017-03-18
 * @author Bo-Xuan Fan
 */
@Slf4j
@Service
public class TestGenerationServiceImpl implements TestGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenerationServiceImpl.class);

    @Autowired
    private QuestionRepository questRepo;

    @Autowired
    private QuestLevelRepository levelRepo;

    @Autowired
    private VersionRepository versionRepo;

    @Value("${com.bxf.hradmin.file.root}")
    private String root;

    @Override
    public Version preview(GenerateCond cond) {
        // 幾種題目類別
        int totalCategories = cond.getCatIds().size();
        // 二微陣列：一維-難易度，二維-各題目類別需出題數
        int[][] categoryQuestionNumbersArray = calcuateQuestionNumbers(cond, totalCategories);

        // 檢查題目數量
        if (!hasEnoughQuestion(cond.getQuestLevels(), categoryQuestionNumbersArray)) {
            throw new TestGenException("題目數量不足");
        }

        // 產生題目
        return doPreview(cond, categoryQuestionNumbersArray);
    }

    private Version doPreview(GenerateCond cond, int[][] categoryQuestionNumbersArray) {
        Version version = new Version();
        List<QuestionSnapshot> questSnapshots = new ArrayList<>();
        for (int i = 0; i < categoryQuestionNumbersArray.length; i++) {
            QuestLevel questLevel = cond.getQuestLevels().get(i);
            LOGGER.debug("Generated Quest Level: {}", questLevel.getId());
            Stream.of(categoryQuestionNumbersArray[i])
                // 根據該類別題數亂數出題
                .forEach((categoryQuestionNumbers) -> {
                    for (int index = 0; index < categoryQuestionNumbers.length; index++) {
                        String catId = cond.getCatIds().get(index);
                        int questNumber = categoryQuestionNumbers[index];
                        LOGGER.debug("\tCategory: {} with {} questions.", catId, questNumber);
                        List<Question> questions = findQuestions(catId, questNumber, cond.getIsSingleAnswer(), questLevel);
                        questSnapshots.addAll(transform(questions));
                    }
                });
        }

        // 設置題號
        for (int i = 0; i < questSnapshots.size(); i++) {
            questSnapshots.get(i).setQuestionNo(i + 1);
        }

        int eachQuestionScore = cond.getTotalScore() / cond.getTotalQuests();
        // 計算建議及格分數
        double passingScore = cond.getQuestLevels().stream()
            .mapToDouble(level -> eachQuestionScore * level.getNumber() * levelRepo.findOne(level.getId()).getCorrectRate())
            .reduce((prev, next) -> prev + next).getAsDouble();
        if (passingScore % eachQuestionScore != 0) {
            passingScore = (((int) (passingScore / eachQuestionScore)) + 1) * eachQuestionScore;
        }
        version.setPassingScore((int) passingScore);
        version.setQuestions(questSnapshots);

        return version;
    }

    private int[][] calcuateQuestionNumbers(GenerateCond cond, int totalCategories) {
        return cond.getQuestLevels().stream()
            // 計算各類別出題數
            .map((level) -> {
                // 平均每類別題數
                int aveQuestNumber = level.getNumber() / totalCategories;
                int[] categoryQuestionNumber = new int[totalCategories];
                for (int i = 0; i < categoryQuestionNumber.length; i++) {
                    categoryQuestionNumber[i] = aveQuestNumber;
                }
                // 將剩餘題數亂數填入各類別
                int mod = level.getNumber() % totalCategories;
                SecureRandom random = new SecureRandom();
                while (mod-- > 0) {
                    int index = random.nextInt(totalCategories);
                    categoryQuestionNumber[index]++;
                }
                return categoryQuestionNumber;
            })
            .toArray(int[][]::new);
    }

    private boolean hasEnoughQuestion(List<QuestLevel> questLevels, int[][] categoryQuestionNumbersArray) {
        // TODO 產生題目前先檢查題庫是否有足夠數量
        return true;
    }

    private List<Question> findQuestions(String catId, int questNumber, Boolean isSingleAnswer, QuestLevel questLevel) {
        Question quest = new Question();
        quest.setCatId(catId);
        quest.setIsSingleAnswer(isSingleAnswer);
        quest.setQuestLevel(questLevel);

        Example<Question> example = Example.of(quest);
        int totalQuestCounts = (int) questRepo.count(example);
        Set<Integer> rowNums = new HashSet<>();
        SecureRandom random = new SecureRandom();
        while (rowNums.size() < questNumber) {
            int index = random.nextInt(totalQuestCounts);
            // Database row id is one-based
            rowNums.add(index + 1);
        }
        Character sqlIsSingleAnswer = null;
        if (isSingleAnswer != null) {
            sqlIsSingleAnswer = isSingleAnswer ? 'Y' : 'N';
        }
        LOGGER.debug("\ttotalQuestCounts:{}, picked up {}", totalQuestCounts, rowNums);
        List<String> oids = questRepo.findRandomOids(catId, sqlIsSingleAnswer, questLevel.getId(), rowNums);
        return questRepo.findByOidIn(oids);
    }

    private List<QuestionSnapshot> transform(List<Question> questions) {
        List<QuestionSnapshot> questSnapshots = questions.stream()
            .map((quest) -> {
                QuestionSnapshot snapshot = new QuestionSnapshot();
                snapshot.setDesc(quest.getDesc());
                List<AnswerSnapshot> answers = quest.getAnswers().stream()
                    .map(answer -> {
                        AnswerSnapshot answerSnapshot = new AnswerSnapshot();
                        answerSnapshot.setDesc(answer.getDesc());
                        answerSnapshot.setCorrect(answer.isCorrect());
                        return answerSnapshot;
                    }).collect(Collectors.toList());
                Collections.shuffle(answers, new Random(System.nanoTime()));
                for (int i = 0; i < answers.size(); i++) {
                    answers.get(i).setAnswerNo((char) ('A' + i));
                }
                snapshot.setAnswers(answers);
                return snapshot;
            })
            .map((quest) -> {
                StringBuilder correctAnswer = new StringBuilder();
                quest.getAnswers().forEach((answer) -> {
                    if (answer.isCorrect()) {
                        correctAnswer.append(answer.getAnswerNo()).append(',');
                    }
                });
                // trim the last ','
                correctAnswer.setLength(correctAnswer.length() - 1);
                quest.setCorrectAnswer(correctAnswer.toString());
                return quest;
            })
            .collect(Collectors.toList());
        Collections.shuffle(questSnapshots, new Random(System.nanoTime()));
        return questSnapshots;
    }

    @Override
    public Version generate(Version version) {
        version.setOid(UUID.randomUUID().toString().replaceAll("-", ""));
        doGenerate(version.getOid(), version.getQuestions());
        version.setCreator("Default Admin");
        version.setCreateDatetime(new Date());
        version.getQuestions().forEach(question -> {
            question.setVersion(version);
            question.getAnswers().forEach(answer -> answer.setQuestion(question));
        });
        versionRepo.save(version);
        return version;
    }

    private void doGenerate(String versionOid, List<QuestionSnapshot> questions) {
        String[] beanNames = { "testAnswerGenerator", "docxTestGenerator", "pdfTestGenerator" };
        for (String beanName : beanNames) {
            BeanUtils.getBean(beanName, TestGenerator.class).generate(versionOid, questions);
        }
    }

    @Override
    public QuestionFile download(String versionOid, String contentType) {
        try {
            QuestionFile questionFile = new QuestionFile();
            File folder = new File(new File(root, "testgen"), versionOid);
            String name;
            switch (contentType) {
                case "docx":
                    name = "T-Java-docx.zip";
                    break;
                case "pdf":
                default:
                    name = "T-Java-pdf.zip";
                    break;
            }
            questionFile.setName(name);
            questionFile.setContentType("application/zip");
            questionFile.setContent(getContent(folder, name));
            return questionFile;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TestGenException(e.getMessage(), ModuleInfo.TestGenMgr);
        }
    }

    private byte[] getContent(File folder, String fileName) throws IOException {
        try (FileInputStream source = new FileInputStream(new File(folder, fileName))) {
            return IOUtils.toByteArray(source);
        }
    }
}
