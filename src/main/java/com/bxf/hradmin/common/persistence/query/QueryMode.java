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
package com.bxf.hradmin.common.persistence.query;

/**
 * QueryMode
 *
 * @since 2017-04-15
 * @author Bo-Xuan Fan
 */
public enum QueryMode {

    /** in */
    IN("in"),
    /** not in */
    NOT_IN("notIn"),
    /** = */
    EQUALS("eq"),
    /** != */
    NOT_EQUALS("ne"),
    /** like */
    LIKE("like"),
    /** not like */
    NOT_LIKE("notLike"),
    /** > */
    GREATER_THAN("gt"),
    /** >= */
    GREATER_EQUALS("ge"),
    /** < */
    LESS_THAN("lt"),
    /** <= */
    LESS_EQUALS("le"),
    /** is null */
    IS_NULL("isNull"),
    /** is null */
    IS_NOT_NULL("isNotNull"),
    /** between and */
    BETWEEN("between"),
    /** or */
    OR("or"),
    /** and */
    AND("and");

    private String code;

    QueryMode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static final QueryMode convert(String code) {
        for (QueryMode searchMode : QueryMode.values()) {
            if (searchMode.getCode().equals(code)) {
                return searchMode;
            }
        }
        // default
        return EQUALS;
    }

    @Override
    public String toString() {
        return code;
    }

}
