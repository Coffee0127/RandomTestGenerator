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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.bxf.hradmin.testgen.service.TestGenerator;

/**
 * AbstractTestGenerator
 *
 * @since 2017-03-25
 * @author Bo-Xuan Fan
 */
public abstract class AbstractTestGenerator implements TestGenerator {

    /** NEW_LINE */
    protected static final String NEW_LINE = "\n";

    @Value("${com.bxf.hradmin.file.root}")
    private String root;

    private File rootPath;

    @PostConstruct
    private void init() {
        rootPath = new File(root, "testgen");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
    }

    protected File getRootPath(String versionOid) {
        File folder = new File(rootPath, versionOid);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}
