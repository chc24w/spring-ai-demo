package com.chc.ai.util;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chc.ai.bean.req.BasePageReq;

/**
 * 分页工具类
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
public class PageUtil {
    /**
     * 构建IPage类型的对象
     */
    public static <T> IPage<T> buildPage(BasePageReq pageReq) {
        return new Page<>(pageReq.getPageNow(),pageReq.getPageSize());
    }
}
