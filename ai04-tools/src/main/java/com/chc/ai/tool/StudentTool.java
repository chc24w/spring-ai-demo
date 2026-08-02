package com.chc.ai.tool;


import cn.hutool.core.util.StrUtil;
import com.chc.ai.bean.vo.StudentVO;
import com.chc.ai.controller.AgentAssistantController;
import com.chc.ai.service.StudentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 定义工具步骤:
 * 1.使用{@link Tool}和{@link ToolParam}注解定义工具以及需要的参数
 * 2.为{@link org.springframework.ai.chat.client.ChatClient}指定系统提供了哪些tool
 * 3.调用{@link AgentAssistantController#testTool(String)}进行测试即可
 * 原理:
 * 1.spring ai会把tools信息传递给大模型 {@link ToolCallingAdvisor}
 * 2.大模型根据用户提示词判断是否需要调用某个工具,并把结果(如需要调用某个工具则含有工具对应的方法以及解析出来的参数)返回
 * 3.spring ai根据结果判断如果需要调用某个工具则进行反射调用,否则正常输出
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Component
@Slf4j
public class StudentTool {
    @Resource
    private StudentService studentService;
    // false:默认,结果查出来后再交给大模型
    @Tool(description = "根据学号查询一个学生", returnDirect = false)
    public StudentVO queryById(
            // 描述需要的参数
            @ToolParam(required = true,description = "学号") String studentNo) {
        // StudentTool.queryById被调用了,studentNo:2024001004
        log.info("StudentTool.queryById被调用了,studentNo:{}", studentNo);
        // 对于一些复杂的工具,业务层一定要做校验,因为大模型自动推算出来的并不一定100%准确
        if (StrUtil.isBlank(studentNo)) {
            return null;
        }
        return studentService.queryByStudentNo(studentNo);
    }
}
