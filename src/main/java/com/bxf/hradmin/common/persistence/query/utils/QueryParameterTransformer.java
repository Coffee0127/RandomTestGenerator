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
package com.bxf.hradmin.common.persistence.query.utils;

import java.lang.reflect.Array;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.bxf.hradmin.common.persistence.query.QueryParameter;

/**
 * QueryParameterTransformer
 *
 * @since 2017-04-15
 * @author Bo-Xuan Fan
 */
@SuppressWarnings("rawtypes")
public final class QueryParameterTransformer {

    private QueryParameterTransformer() {
    }

    public static Predicate generatePredicate(Root root, CriteriaBuilder builder, QueryParameter... queryParameters) {
        Predicate condition = builder.conjunction();
        for (QueryParameter queryParameter : queryParameters) {
            Object value = queryParameter.getValue();
            if (value == null || StringUtils.isBlank(value.toString())) {
                continue;
            }
            Path path = root.get(queryParameter.getKey());
            condition = doGenerateCondition(builder, condition, queryParameter, value, path);
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    private static Predicate doGenerateCondition(CriteriaBuilder builder,
            Predicate condition, QueryParameter queryParameter, Object value,
            Path path) {
        switch (queryParameter.getMode()) {
            case BETWEEN:
                Object[] values = asArray(value);
                if (values != null) {
                    return builder.and(builder.between((Path<Comparable>) path, asComparable(values[0]), asComparable(values[1])));
                }
                return condition;
            case GREATER_THAN:
                return builder.and(condition, builder.greaterThan((Path<Comparable>) path, asComparable(value)));
            case GREATER_EQUALS:
                return builder.and(condition, builder.greaterThanOrEqualTo((Path<Comparable>) path, asComparable(value)));
            case LESS_THAN:
                return builder.and(condition, builder.lessThan((Path<Comparable>) path, asComparable(value)));
            case LESS_EQUALS:
                return builder.and(condition, builder.lessThanOrEqualTo((Path<Comparable>) path, asComparable(value)));
            case IS_NULL:
                return builder.and(condition, builder.isNull(path));
            case IS_NOT_NULL:
                return builder.and(condition, builder.isNotNull(path));
            case IN:
                return builder.and(condition, path.in(asArray(value)));
            case NOT_IN:
                return builder.and(condition, builder.not(path.in(asArray(value))));
            case LIKE:
                return builder.and(condition, builder.like(path, "%" + value + "%"));
            case NOT_LIKE:
                return builder.and(condition, builder.notLike(path, "%" + value + "%"));
            case EQUALS:
                return builder.and(condition, builder.equal(path, value));
            case NOT_EQUALS:
                return builder.and(condition, builder.notEqual(path, value));
            default:
                return condition;
        }
    }

    private static Object[] asArray(Object value) {
        if (value.getClass().isArray()) {
            Object[] result = new Object[Array.getLength(value)];
            for (int i = 0; i < result.length; ++i) {
                result[i] = Array.get(value, i);
            }
            return result;
        } else if (value instanceof Collection) {
            return ((Collection) value).toArray();
        }
        return new Object[] { value };
    }

    private static Comparable asComparable(Object value) {
        if (value instanceof Comparable) {
            return (Comparable) value;
        }
        throw new IllegalArgumentException(value + " cannot be cast to java.lang.Comparable");
    }
}
