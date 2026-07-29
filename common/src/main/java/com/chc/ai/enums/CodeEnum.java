package com.chc.ai.enums;

import lombok.Getter;
/**
 * 响应码枚举类
 * @author ${authorName}
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Getter
public enum  CodeEnum implements BaseEnum<Integer> {
    SUCCESS(200,"成功"),

    PARAM_ERR(10000,"参数不正确"),

    DB_ERR(20000,"数据库异常"),
    DUPLICATE_KEY_ERR(20001,"唯一性约束校验失败"),

    BIZ_ERR(30000,"业务异常"),
    AUTH_ERR(30001,"权限不足"),

    SYS_ERR(90000,"系统异常"),
    ;
    CodeEnum(Integer code,String desc) {
        this.code = code;
        this.desc = desc;
    }
    private final Integer code;
    private final String desc;
}
