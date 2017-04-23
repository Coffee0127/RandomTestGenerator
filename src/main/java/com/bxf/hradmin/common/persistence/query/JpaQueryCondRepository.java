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

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 繼承 {@linkplain org.springframework.data.jpa.repository.JpaRepository JpaRepository} 及 {@linkplain org.springframework.data.jpa.repository.JpaSpecificationExecutor JpaSpecificationExecutor},
 * 並加入 {@linkplain com.bxf.hradmin.common.persistence.query.QueryCond QueryCond} 查詢
 *
 * @since 2017-04-15
 * @author Bo-Xuan Fan
 */
@NoRepositoryBean
public interface JpaQueryCondRepository<T, S extends Serializable, Q extends QueryCond<T>>
    extends JpaRepository<T, S>, JpaSpecificationExecutor<T> {

    default Page<T> find(Q queryCond) {
        return this.findAll(queryCond, queryCond.toPageable());
    }
}
