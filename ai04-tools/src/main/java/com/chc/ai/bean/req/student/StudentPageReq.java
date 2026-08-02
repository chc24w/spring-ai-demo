package com.chc.ai.bean.req.student;

import com.chc.ai.bean.req.BasePageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生分页查询入参
 *
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentPageReq extends BasePageReq {
    /**
     * 学号
     */
    private String studentNo;
    /**
     * 姓名
     */
    private String studentName;
}
