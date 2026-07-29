package com.chc.ai.controller;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
/**
 * 图片模型ImageModel==>wanx2
 */
@RestController
@Slf4j
@RequestMapping("/dashScope/imageModel")
public class DashScopeImageModelController {


    @Resource
    @Qualifier("dashScopeImageModel")
    private ImageModel imageModel;

    /**
     * 文本转图像
     * http://localhost:8080/dashScope/imageModel/t2i?input=一位落寞的侠客背影
     * # 万相文生图api文档: https://bailian.console.aliyun.com/cn-beijing?tab=api#/api/?type=model&url=2862677
     */
    @GetMapping("/t2i")
    public List<String> generateImage(@RequestParam("input") String input) {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                // 生成几张
                .n(1)
                // 宽高
                .width(1280)
                .height(1280)
                // 通义万相
                .model("wanx2.1-t2i-turbo").build();
        ImageResponse imageResponse = imageModel.call(new ImagePrompt(input, options));
        List<String> list = new ArrayList<>();
        for (ImageGeneration result : imageResponse.getResults()) {
            String url = result.getOutput().getUrl();
            System.out.println("生成图片的url:" + url);
            String b64Json = result.getOutput().getB64Json();
            System.out.println("图片的base64:" + b64Json);
            list.add(url);
        }
        return list;
    }
}
