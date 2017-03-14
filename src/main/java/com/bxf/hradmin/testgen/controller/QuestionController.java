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
package com.bxf.hradmin.testgen.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bxf.hradmin.testgen.dto.GenerateCond;
import com.bxf.hradmin.testgen.model.QuestLevel;
import com.bxf.hradmin.testgen.model.QuestionSnapshot;
import com.bxf.hradmin.testgen.repository.QuestLevelRepository;
import com.bxf.hradmin.testgen.service.TestGenerator;

/**
 * QuestionController
 *
 * @since 2017-02-26
 * @author Bo-Xuan Fan
 */
@RestController
@RequestMapping("/quest")
public class QuestionController {

    @Autowired
    private QuestLevelRepository levelRepo;

    @Autowired
    private TestGenerator generator;

    @RequestMapping("/findQL")
    public List<QuestLevel> findAllLevels() {
        return levelRepo.findAll();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/generate")
    public List<QuestionSnapshot> find(@RequestBody(required = false) Map<String, Object> conditions) {
        GenerateCond cond = new GenerateCond();
        cond.setTotalQuests((Integer) conditions.get("totalQuests"));
        cond.setTotalQuests((Integer) conditions.get("totalScore"));
        cond.setCatIds((List<String>) conditions.get("catIds"));
        List<QuestLevel> questLevels = ((List<Map<String, Object>>) conditions.get("questLevels"))
                .stream().map((v) -> {
                    QuestLevel level = new QuestLevel();
                    level.setId((Integer) v.get("id"));
                    level.setNumber((Integer) v.get("number"));
                    return level;
                }).collect(Collectors.toList());
        cond.setQuestLevels(questLevels);
        cond.setIsSingleAnswer((Boolean) conditions.get("isSingleAnswer"));
        return generator.generate(cond);
    }
}
