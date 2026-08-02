package com.chc.ai.controller.image;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeImageAutoConfiguration;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeImageProperties;
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
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/dashScope/imageModel")
public class DashScopeImageModelController {
    /**
     * 在{@link DashScopeImageAutoConfiguration}中自动注入
     */
    @Resource
    @Qualifier("dashScopeImageModel")
    private ImageModel imageModel;
    @Resource
    private DashScopeImageProperties dashScopeImageProperties;

    /**
     * 文本转图像
     * http://localhost:8080/dashScope/imageModel/t2i?input=一位落寞的侠客背影
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
