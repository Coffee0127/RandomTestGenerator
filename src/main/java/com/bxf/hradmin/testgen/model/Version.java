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
package com.bxf.hradmin.testgen.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bxf.hradmin.common.persistence.IDataObject;
import com.bxf.hradmin.common.persistence.OidGeneratorListener;

import lombok.Data;

/**
 * 測驗考卷版本
 *
 * @since 2017-02-25
 * @author Bo-Xuan Fan
 */
@Entity
@Table(name = "version")
@EntityListeners(OidGeneratorListener.class)
@Data
public class Version implements IDataObject {

    @Id
    @Column(length = 32)
    private String oid;

    /** 建立時間 */
    @Column(name = "create_datetime", nullable = false)
    private Date createDatetime;

    /** 建立者 */
    @Column(name = "creator", length = 200, nullable = false)
    private String creator;

    /** 及格分數 */
    @Column(name = "passing_score", nullable = false)
    private Integer passingScore;

    /** 問題清單 */
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "version")
    private List<QuestionSnapshot> questions;
}
