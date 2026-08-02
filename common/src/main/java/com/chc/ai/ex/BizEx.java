package com.chc.ai.ex;

import com.chc.ai.enums.CodeEnum;
import lombok.Getter;

/**
 * @author ${authorName}
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Getter
public class BizEx extends RuntimeException {
    private Integer code;
    public BizEx(String msg) {
        super(msg);
        this.code = CodeEnum.BIZ_ERR.getCode();
    }
    public BizEx(CodeEnum codeEnum) {
        super(codeEnum.getDesc());
        this.code = codeEnum.getCode();
    }
    public BizEx(Integer code,String msg) {
        super(msg);
        this.code = code;
    }
}
