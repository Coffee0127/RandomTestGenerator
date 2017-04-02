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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bxf.hradmin.testgen.dto.GenerateCond;
import com.bxf.hradmin.testgen.dto.QuestionFile;
import com.bxf.hradmin.testgen.model.QuestLevel;
import com.bxf.hradmin.testgen.model.Version;
import com.bxf.hradmin.testgen.repository.QuestLevelRepository;
import com.bxf.hradmin.testgen.service.TestGenException;
import com.bxf.hradmin.testgen.service.TestGenerationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private TestGenerationService testGenService;

    @RequestMapping("/findQL")
    public List<QuestLevel> findAllLevels() {
        return levelRepo.findAll();
    }

    @RequestMapping("/preview")
    public Version preview(@RequestBody Map<String, Object> parameter) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            GenerateCond cond = mapper.readValue(mapper.writeValueAsString(parameter),
                    new TypeReference<GenerateCond>() { });
            return testGenService.preview(cond);
        } catch (IOException e) {
            throw new TestGenException(e);
        }
    }

    @RequestMapping("/generate")
    public String generate(@RequestBody Map<String, Object> parameter) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Version questions = mapper.readValue(mapper.writeValueAsString(parameter),
                    new TypeReference<Version>() { });
            return testGenService.generate(questions).getOid();
        } catch (IOException e) {
            throw new TestGenException(e);
        }
    }

    @RequestMapping("/download")
    public void download(@RequestParam Map<String, Object> parameter, HttpServletResponse response) throws IOException {
        String versionOid = (String) parameter.get("versionOid");
        String contentType = (String) parameter.get("contentType");
        QuestionFile questionFile = testGenService.download(versionOid, contentType);
        String contentDisposition = new StringBuilder()
                .append("attachment; filename=\"").append(questionFile.getName()).append('"').toString();
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentType(questionFile.getContentType());
        InputStream input = new ByteArrayInputStream(questionFile.getContent());
        IOUtils.copy(input, response.getOutputStream());
        response.flushBuffer();
    }
}
