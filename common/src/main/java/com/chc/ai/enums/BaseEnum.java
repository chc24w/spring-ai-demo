package com.chc.ai.enums;
/**
 * 枚举类顶级接口
 * @author ${authorName}
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
public interface BaseEnum<E> {
    E getCode();
    String getDesc();
}
