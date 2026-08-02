package com.chc.ai.bean.req.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学生新增入参类
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Data
public class StudentAddReq {
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
