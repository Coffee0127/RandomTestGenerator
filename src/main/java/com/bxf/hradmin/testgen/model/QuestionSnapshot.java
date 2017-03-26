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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bxf.hradmin.common.persistence.IDataObject;
import com.bxf.hradmin.common.persistence.OidGeneratorListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * QuestionSnapshot
 *
 * @since 2017-02-25
 * @author Bo-Xuan Fan
 */
@Entity
@Table(name = "question_snapshot")
@EntityListeners(OidGeneratorListener.class)
@Data
public class QuestionSnapshot implements IDataObject {

    @Id
    @Column(length = 32)
    private String oid;

    /** 問卷版本 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_oid", nullable = false)
    @JsonIgnore
    private Version version;

    /** 問題編號 */
    @Column(name = "question_no", nullable = false)
    private Integer questionNo;

    /** 問題描述 */
    @Column(name = "description", length = 4000, nullable = false)
    private String desc;

    /** 正確解答 */
    @Column(name = "correct_answer", length = 10, nullable = false)
    private String correctAnswer;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "question")
    private List<AnswerSnapshot> answers;

}
