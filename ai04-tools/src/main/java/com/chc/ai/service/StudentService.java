package com.chc.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chc.ai.bean.entity.StudentDO;
import com.chc.ai.bean.req.student.StudentAddReq;
import com.chc.ai.bean.req.student.StudentPageReq;
import com.chc.ai.bean.req.student.StudentUpdateReq;
import com.chc.ai.bean.vo.StudentVO;
import com.chc.ai.res.Result;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
public interface StudentService extends IService<StudentDO> {
    /**
     * 根据主键查询
     */
    StudentVO queryById(Long id);

    /**
     * 根据学号查询
     */
    StudentVO queryByStudentNo(String studentNo);

    /**
     * 根据id修改信息
     */
    void modifyById(StudentUpdateReq updateReq);
    /**
     * 新增
     */
    void add(StudentAddReq addReq);
    /**
     * 分页查询
     */
    Result<List<StudentVO>> queryPage(StudentPageReq pageReq);
    /**
     * 按主键删除
     */
    void deleteById(Long id);
}
