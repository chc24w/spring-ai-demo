package com.chc.ai.bean.req.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学生修改请求参数
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Data
public class StudentUpdateReq {
    /**
     * 主键
     */
    @NotNull
    private Long id;
    /**
     * 学号
     */
    @NotBlank
    private String studentNo;
    /**
     * 姓名
     */
    @NotBlank
    private String studentName;
}
