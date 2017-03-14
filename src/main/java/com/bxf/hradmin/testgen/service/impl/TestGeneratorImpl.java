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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.bxf.hradmin.testgen.dto.GenerateCond;
import com.bxf.hradmin.testgen.model.AnswerSnapshot;
import com.bxf.hradmin.testgen.model.Question;
import com.bxf.hradmin.testgen.model.QuestionSnapshot;
import com.bxf.hradmin.testgen.repository.QuestionRepository;
import com.bxf.hradmin.testgen.service.TestGenerator;

/**
 * TestGeneratorImpl
 *
 * @since 2017-02-26
 * @author Bo-Xuan Fan
 */
@Service
public class TestGeneratorImpl implements TestGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGeneratorImpl.class);

    @Autowired
    private QuestionRepository questRepo;

    @Override
    public List<QuestionSnapshot> generate(GenerateCond cond) {
        List<QuestionSnapshot> questSnapshots = new ArrayList<>();
        // 幾種題目類別
        int totalCategories = cond.getCatIds().size();
        cond.getQuestLevels().stream()
            // 計算各類別出題數
            .map((level) -> {
                // 平均每類別題數
                int aveQuestNumber = level.getNumber() / totalCategories;
                int[] categoryQuestionNumber = new int[totalCategories];
                for (int j = 0; j < categoryQuestionNumber.length; j++) {
                    categoryQuestionNumber[j] = aveQuestNumber;
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
            // 根據該類別題數亂數出題
            .forEach((categoryQuestionNumbers) -> {
                for (int index = 0; index < categoryQuestionNumbers.length; index++) {
                    LOGGER.debug("Category: {} with {} questions.", cond.getCatIds().get(index), categoryQuestionNumbers[index]);
                    String catId = cond.getCatIds().get(index);
                    List<Question> questions = doGenerate(catId, categoryQuestionNumbers[index], cond.getIsSingleAnswer());
                    questSnapshots.addAll(transform(questions));
                }
            });
        return questSnapshots;
    }

    private List<Question> doGenerate(String catId, int questNumber, Boolean isSingleAnswer) {
        Question quest = new Question();
        quest.setCatId(catId);
        quest.setIsSingleAnswer(isSingleAnswer);
        Example<Question> example = Example.of(quest);
        // TODO avoid fetch all datas
        List<Question> rawlist = questRepo.findAll(example);
        Set<Integer> indexes = getQuestIndexes(questNumber);
        List<Question> questions = indexes.stream().map((index) -> {
            return rawlist.get(index);
        }).collect(Collectors.toList());
        return questions;
//        long totalQuestions = questRepo.count(example);
//        int pageSize = questNumber;
//        int totalPage = (int) (totalQuestions / pageSize);
//        if (totalQuestions % pageSize != 0) {
//            totalPage += 1;
//        }
//        int page = new SecureRandom().nextInt(totalPage);
//        LOGGER.debug("\ttotalQuestions:{}, pageSize:{}, totalPage:{}, page:{}", totalQuestions, pageSize, totalPage, page);
//        Pageable pageable = new PageRequest(page, pageSize);
//        return questRepo.findAll(example, pageable).getContent();
    }

    private Set<Integer> getQuestIndexes(int questNumber) {
        Set<Integer> indexes = new HashSet<>();
        SecureRandom random = new SecureRandom();
        while (indexes.size() < questNumber) {
            indexes.add(random.nextInt(questNumber));
        }
        return indexes;
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

}
