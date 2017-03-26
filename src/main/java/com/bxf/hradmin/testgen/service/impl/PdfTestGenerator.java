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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xwpf.converter.core.IXWPFConverter;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import com.bxf.hradmin.common.exception.ModuleInfo;
import com.bxf.hradmin.testgen.model.QuestionSnapshot;
import com.bxf.hradmin.testgen.service.TestGenException;

import lombok.extern.slf4j.Slf4j;

/**
 * PdfTestGenerator
 *
 * @since 2017-03-25
 * @author Bo-Xuan Fan
 */
@Slf4j
@Service("pdfTestGenerator")
public class PdfTestGenerator extends AbstractTestGenerator {

    @Override
    public void generate(String fileName, List<QuestionSnapshot> questions) {
        try (InputStream source = new FileInputStream(new File(getRootPath(), fileName.replace(".pdf", ".docx")));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(getRootPath(), fileName)))) {

            IXWPFConverter<PdfOptions> converter = PdfConverter.getInstance();
            XWPFDocument document = new XWPFDocument(source);
            PdfOptions options = PdfOptions.getDefault();
            converter.convert(document, bos, options);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TestGenException(e.getMessage(), ModuleInfo.TestGenMgr);
        }
    }

}
