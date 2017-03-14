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
package com.bxf.hradmin.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * BeanUtils
 *
 * @since 2017年3月7日
 * @author Bo-Xuan Fan
 */
@Component
public final class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private BeanUtils() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 取得 Spring 定義之Bean
     *
     * @param <T>
     *            Bean所屬類別
     * @param clazz
     *            Bean所屬類別
     * @return T Spring Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 取得 Spring 定義之Bean
     *
     * @param beanName
     *            Spring Bean
     * @param args
     *            建構參數
     * @return Object Spring Bean
     */
    public static Object getBean(String beanName, Object... args) {
        return applicationContext.getBean(beanName, args);
    }

    /**
     * 取得 Spring 定義之Bean
     *
     * @param <T>
     *            Bean所屬類別
     * @param beanID
     *            BeanID
     * @param clazz
     *            Bean所屬類別
     * @return T Spring Bean
     */
    public static <T> T getBean(String beanID, Class<T> clazz) {
        return applicationContext.getBean(beanID, clazz);
    }

    /**
     * 給定clazz產生該實體
     * @param clazz class to instantiate
     * @return the new instance
     */
    public static <T> T instantiate(Class<T> clazz) {
        return org.springframework.beans.BeanUtils.instantiate(clazz);
    }

    /**
     * 複製 source bean 的屬性到 target bean
     * @param source the source bean
     * @param target the target bean
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * 將source物件轉成TARGET型態的物件
     * @param <T> target
     * @param <S> source
     * @param clz TARGET class
     * @param source source object
     * @return object which class is TARGET
     */
    public static <T, S> T copyProperties(Class<T> clz, S source) {
        T target = BeanUtils.instantiate(clz);
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
