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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bxf.hradmin.common.exception.ModuleInfo;
import com.bxf.hradmin.testgen.model.QuestionSnapshot;
import com.bxf.hradmin.testgen.service.TestGenException;
import com.bxf.hradmin.testgen.service.TestGenerator;

import lombok.extern.slf4j.Slf4j;

/**
 * DocxTestGenerator
 *
 * @since 2017-02-26
 * @author Bo-Xuan Fan
 */
@Slf4j
@Service("docxTestGenerator")
public class DocxTestGenerator extends AbstractTestGenerator {

    @Value("${com.bxf.hradmin.testgen.service.font-size}")
    private int fontSize;

    @Value("${com.bxf.hradmin.testgen.service.font-ascii-family}")
    private String fontAsciiFamily;

    @Value("${com.bxf.hradmin.testgen.service.font-eastAsia-family}")
    private String fontEastAsiaFamily;

    @Override
    public void generate(String fileName, List<QuestionSnapshot> questions) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(getRootPath(), fileName)));
                InputStream is = TestGenerator.class.getResourceAsStream("/template/questions.docx")) {

            XWPFDocument doc = new XWPFDocument(is);
            replaceWordHolder(doc);

            XWPFTable table = doc.getTables().get(0);
            for (QuestionSnapshot question : questions) {
                XWPFTableRow row = table.createRow();
                writeQuestionNo(question.getQuestionNo(), row);
                writeQuestion(question, row);
            }

            // 刪除 template row
            table.removeRow(0);
            // 設定邊框
            setBorders(table);
            // 產製檔案
            doc.write(bos);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TestGenException(e.getMessage(), ModuleInfo.TestGenMgr);
        }
    }

    private void replaceWordHolder(XWPFDocument doc) {
        doc.getParagraphs().forEach(paragraph -> {
            Map<String, String> data = new HashMap<>();
            data.put("score", "4");
            doReplace(paragraph, data);
        });

        doc.getFooterList().forEach(footer ->
            footer.getParagraphs().forEach(paragraph -> {
                Map<String, String> data = new HashMap<>();
                data.put("year", new SimpleDateFormat("yyyy").format(new Date()));
                doReplace(paragraph, data);
            })
        );
    }

    private void doReplace(XWPFParagraph p, Map<String, String> data) {
        String pText = p.getText(); // complete paragraph as string
        if (pText.contains("${")) { // if paragraph does not include our pattern, ignore
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(pText);
            while (matcher.find()) { // for all patterns in the paragraph
                String key = matcher.group(1);  // extract key start and end pos
                int start = matcher.start(1);
                int end = matcher.end(1);
                String value = data.get(key);
                if (value == null) {
                    value = "";
                }
                // get runs which contain the pattern
                SortedMap<Integer, XWPFRun> range = getPosToRuns(p).subMap(start - 2, true, end + 1, true);
                boolean isFoundDollarSign = false;
                boolean isFoundLeftBracket = false;
                boolean isFoundRightBracket = false;
                XWPFRun prevRun = null; // previous run handled in the loop
                XWPFRun found2Run = null; // run in which { was found
                int found2Pos = -1; // pos of { within above run
                for (XWPFRun run : range.values()) {
                    if (run == prevRun) {
                        continue; // this run has already been handled
                    }
                    if (isFoundRightBracket) {
                        break; // done working on current key pattern
                    }
                    prevRun = run;
                    for (int k = 0;; k++) { // iterate over texts of run r
                        if (isFoundRightBracket) {
                            break;
                        }
                        String txt = null;
                        try {
                            txt = run.getText(k); // note: should return null, but throws exception if the text does not exist
                        } catch (Exception e) {
                        }
                        if (txt == null) {
                            break; // no more texts in the run, exit loop
                        }
                        if (txt.contains("$") && !isFoundDollarSign) {  // found $, replace it with value from data map
                            txt = txt.replaceFirst("\\$", value);
                            isFoundDollarSign = true;
                        }
                        if (txt.contains("{") && !isFoundLeftBracket && isFoundDollarSign) {
                            found2Run = run; // found { replace it with empty string and remember location
                            found2Pos = txt.indexOf('{');
                            txt = txt.replaceFirst("\\{", "");
                            isFoundLeftBracket = true;
                        }

                        // find } and set all chars between { and } to blank
                        if (isFoundDollarSign && isFoundLeftBracket && !isFoundRightBracket) {
                            if (txt.contains("}")) {
                                if (run == found2Run) { // complete pattern was within a single run
                                    txt = txt.substring(0, found2Pos) + txt.substring(txt.indexOf('}'));
                                } else {
                                    txt = txt.substring(txt.indexOf('}'));
                                }
                            } else if (run == found2Run) {
                                txt = txt.substring(0,  found2Pos);
                            } else {
                                txt = ""; // run between { and }, set text to blank
                            }
                        }
                        if (txt.contains("}") && !isFoundRightBracket) {
                            txt = txt.replaceFirst("\\}", "");
                            isFoundRightBracket = true;
                        }
                        run.setText(txt, k);
                    }
                }
            }
        }
    }

    private NavigableMap<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph) {
        int pos = 0;
        NavigableMap<Integer, XWPFRun> map = new TreeMap<>();
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(run.getTextPosition());
            if (runText != null && runText.length() > 0) {
                for (int i = 0; i < runText.length(); i++) {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }
        }
        return map;
    }

    private void writeQuestionNo(Integer questionNo, XWPFTableRow row) {
        XWPFTableCell cell = row.getCell(0);
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        adjustLineHeight(paragraph);
        // 題號置中
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        // 調整欄寬
        cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(735));

        XWPFRun run = createRun(paragraph);
        run.setText(String.valueOf(questionNo));
    }

    private void writeQuestion(QuestionSnapshot question, XWPFTableRow row) {
        // 顯示題目 & 答案
        StringBuilder builder = new StringBuilder().append(question.getDesc());
        builder.append(NEW_LINE);
        question.getAnswers().forEach(answer ->
        builder.append(NEW_LINE)
        .append(answer.getAnswerNo())
        .append(". ")
        .append(answer.getDesc())
                );
        String desc = builder.toString();
        XWPFTableCell cell = row.getCell(1);
        cell.removeParagraph(0);

        String[] lines = desc.split(NEW_LINE);
        for (String line : lines) {
            // add new line and insert new text
            XWPFParagraph paragraph = cell.addParagraph();
            // 題目 & 答案左右對齊
            paragraph.setAlignment(ParagraphAlignment.BOTH);
            adjustLineHeight(paragraph);
            XWPFRun run = createRun(paragraph);
            run.setText(line);
        }
    }

    private XWPFRun createRun(XWPFParagraph paragraph) {
        // 設定字型 & 大小
        XWPFRun run = paragraph.createRun();
        run.setFontSize(fontSize);
        CTFonts rFonts = run.getCTR().getRPr().addNewRFonts();
        rFonts.setEastAsia(fontEastAsiaFamily);
        rFonts.setAscii(fontAsciiFamily);
        return run;
    }

    private void adjustLineHeight(XWPFParagraph paragraph) {
        // 調整段落間距
        paragraph.setSpacingAfter(0);
        paragraph.setSpacingBefore(0);
        // 調整行距
        CTSpacing spacing = paragraph.getCTP().getPPr().getSpacing();
        spacing.setLineRule(STLineSpacingRule.EXACT);
        spacing.setLine(BigInteger.valueOf(360));
    }

    private void setBorders(XWPFTable table) {
        // 設置邊框
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblBorders borders = tblPr.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
    }
}
