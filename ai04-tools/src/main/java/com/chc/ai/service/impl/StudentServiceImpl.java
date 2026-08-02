package com.chc.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chc.ai.bean.entity.StudentDO;
import com.chc.ai.bean.req.student.StudentAddReq;
import com.chc.ai.bean.req.student.StudentPageReq;
import com.chc.ai.bean.req.student.StudentUpdateReq;
import com.chc.ai.bean.vo.StudentVO;
import com.chc.ai.mapper.StudentMapper;
import com.chc.ai.res.Result;
import com.chc.ai.service.StudentService;
import com.chc.ai.util.ConvertUtil;
import com.chc.ai.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学生表业务实现类
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, StudentDO> implements StudentService {
    @Override
    public StudentVO queryById(Long id) {
        StudentDO existedEntity = getById(id);
        return ConvertUtil.convert(existedEntity, StudentVO.class);
    }

    @Override
    public StudentVO queryByStudentNo(String studentNo) {
        LambdaQueryWrapper<StudentDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StudentDO::getStudentNo,studentNo);
        return ConvertUtil.convert(getOne(lqw), StudentVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyById(StudentUpdateReq updateReq) {
        StudentDO entity = ConvertUtil.convert(updateReq,StudentDO.class);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(StudentAddReq addReq) {
        StudentDO entity = ConvertUtil.convert(addReq,StudentDO.class);
        save(entity);
    }

    @Override
    public Result<List<StudentVO>> queryPage(StudentPageReq pageReq) {
        IPage<StudentDO> iPage = PageUtil.buildPage(pageReq);
        LambdaQueryWrapper<StudentDO> lqw = new LambdaQueryWrapper<>();
        // 拼接查询条件
        lqw.like(StrUtil.isNotBlank(pageReq.getStudentNo()),StudentDO::getStudentNo,pageReq.getStudentNo());
        lqw.like(StrUtil.isNotBlank(pageReq.getStudentName()),StudentDO::getStudentName,pageReq.getStudentName());
        lqw.orderByDesc(StudentDO::getId);
        IPage<StudentDO> page = page(iPage, lqw);
        return ConvertUtil.convert(page, StudentVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        removeById(id);
    }
}
