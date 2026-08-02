package com.chc.ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Component
@Slf4j
public class WeatherTool {
    @Tool(description = "根据城市名称查询该城市的天气", returnDirect = true)
    public String getWeather(
            @ToolParam(description = "城市名称") String cityName) {
        // WeatherTool.getWeather被调用了,cityName:北京
        log.info("WeatherTool.getWeather被调用了,cityName:{}", cityName);
        return cityName + "大雪";
    }
}
