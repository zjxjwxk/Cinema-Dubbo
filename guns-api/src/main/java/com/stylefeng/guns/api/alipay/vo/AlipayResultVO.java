package com.stylefeng.guns.api.alipay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlipayResultVO implements Serializable {

    private String orderId;
    private Integer orderStatus;
    private String orderMsg;
}
