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
package com.bxf.hradmin.testgen.dto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.bxf.hradmin.common.persistence.query.QueryCond;
import com.bxf.hradmin.common.persistence.query.QueryMode;
import com.bxf.hradmin.common.persistence.query.QueryParameter;
import com.bxf.hradmin.common.persistence.query.utils.QueryParameterTransformer;
import com.bxf.hradmin.testgen.model.Question;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QuestionQueryCond
 *
 * @since 2017-04-09
 * @author Bo-Xuan Fan
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QuestionQueryCond extends QueryCond<Question> {

    /** 問題類別 */
    private String catId;

    /** 是否為單選 */
    private Boolean isSingleAnswer;

    /** 題目難易度 */
    private Integer levelId;

    @Override
    public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        QueryParameter[] queryParameters = new QueryParameter[3];
        queryParameters[0] = new QueryParameter(QueryMode.EQUALS, "catId", this.catId);
        queryParameters[1] = new QueryParameter(QueryMode.EQUALS, "isSingleAnswer", this.isSingleAnswer);
        queryParameters[2] = new QueryParameter(QueryMode.EQUALS, "levelId", this.levelId);
        return QueryParameterTransformer.generatePredicate(root, builder, queryParameters);
    }

}
