package com.chc.ai.bean;

import lombok.Data;

@Data
public class Address {
    /**
     * 收货人
     */
    private String receiverName;
    /**
     * 电话
     */
    private String receiverPhone;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区/县
     */
    private String district;
    /**
     * 明细地址
     */
    private String detail;
}