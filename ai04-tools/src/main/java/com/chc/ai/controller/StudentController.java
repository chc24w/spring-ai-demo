package com.chc.ai.controller;

import com.chc.ai.bean.req.student.StudentAddReq;
import com.chc.ai.bean.req.student.StudentPageReq;
import com.chc.ai.bean.req.student.StudentUpdateReq;
import com.chc.ai.bean.vo.StudentVO;
import com.chc.ai.res.Result;
import com.chc.ai.service.StudentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@RequestMapping("/student")
@Validated
@Slf4j
public class StudentController {
    @Resource
    private StudentService studentService;
    /**
     * http://localhost:8080/student/queryById?id=1
     */
    @GetMapping("/queryById")
    public Result<StudentVO> queryById(@RequestParam("id") Long id) {
        return Result.buildSuccess(studentService.queryById(id));
    }

    /**
     * http://localhost:8080/student/queryByStudentNo?studentNo=2024001003
     */
    @GetMapping("/queryByStudentNo")
    public Result<StudentVO> queryByStudentNo(@RequestParam("studentNo") String studentNo) {
        return Result.buildSuccess(studentService.queryByStudentNo(studentNo));
    }

    @PostMapping("/modifyById")
    public Result<Void> modifyById(@RequestBody @Validated StudentUpdateReq updateReq) {
        studentService.modifyById(updateReq);
        return Result.buildSuccessMsg("修改成功");
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Validated StudentAddReq addReq) {
        studentService.add(addReq);
        return Result.buildSuccessMsg("新增成功");
    }

    @PostMapping("/queryPage")
    public Result<List<StudentVO>> queryPage(@RequestBody StudentPageReq pageReq) {
        return studentService.queryPage(pageReq);
    }

    @GetMapping("/deleteById")
    public Result<Void> deleteById(@RequestParam("id") Long id) {
        studentService.deleteById(id);
        return Result.buildSuccessMsg("删除成功");
    }
}
