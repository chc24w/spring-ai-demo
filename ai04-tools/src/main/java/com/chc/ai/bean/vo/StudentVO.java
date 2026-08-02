package com.chc.ai.bean.vo;

import lombok.Data;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Data
public class StudentVO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 学号
     */
    private String studentNo;
    /**
     * 姓名
     */
    private String studentName;
}
