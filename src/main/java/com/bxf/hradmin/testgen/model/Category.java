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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bxf.hradmin.common.persistence.IDataObject;
import com.bxf.hradmin.common.persistence.OidGeneratorListener;

import lombok.Data;

/**
 * 問題種類
 * <li>Java Building Blocks
 * <li>Operators and Statements
 * <li>Core Java APIs
 * <li>Methods and Encapsulation
 * <li>Class Design
 * <li>Exceptions
 *
 * @since 2017-02-24
 * @author Bo-Xuan Fan
 */
@Entity
@Table(name = "category")
@EntityListeners(OidGeneratorListener.class)
@Data
public class Category implements IDataObject {

    @Id
    @Column(length = 32)
    private String oid;

    /** 問題種類描述 */
    @Column(name = "description", length = 4000, nullable = false)
    private String desc;

}
