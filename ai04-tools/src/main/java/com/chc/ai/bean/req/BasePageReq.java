package com.chc.ai.bean.req;

import com.chc.ai.constants.CommonConstants;
import lombok.Data;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Data
public class BasePageReq {
    /**
     * 当前第几页,从1开始
     */
    private Long pageNow = CommonConstants.DEFAULT_PAGE_NOW;
    /**
     * 每页有多少条
     */
    private Long pageSize = CommonConstants.DEFAULT_PAGE_SIZE;

    public Long getStart() {
        return  (pageNow - 1) * pageSize;
    }
}
