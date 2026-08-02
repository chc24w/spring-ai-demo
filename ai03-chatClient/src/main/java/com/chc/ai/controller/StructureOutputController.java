package com.chc.ai.controller;

import com.chc.ai.bean.Address;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/structureOutput")
public class StructureOutputController {
    @Resource
    private ChatClient chatClient;
    /**
     * 结构化输出,如:提取发票信息,图片识别结果存储
     * http://localhost:8080/structureOutput/test1
     */
    @GetMapping("/test1")
    public Address test1() {
        String input = "收货人:小明,电话:13023601427,地址:河北省邯郸市民主县非洲大道222号兰花小区7栋777";
        Address result = chatClient.prompt()
                .system("""
                        提取用户输入的收货信息
                        """)
                .user(input)
                .call()
                // 会在context中增加一个key:spring.ai.chat.client.output.format
                // value: 结构化输出spring ai增加的提示词.txt
                // 具体映射到哪个字段与注释无关,所以变量名要见名知意
                .entity(Address.class);
        return result;
    }
}
