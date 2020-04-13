package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;

public interface AlipayServiceApi {

    /**
     * 预下单并获取二维码地址
     * @param orderId 订单编号
     * @return AlipayInfoVO
     */
    AlipayInfoVO getQRCode(String orderId);

    /**
     * 获取订单状态
     * @param orderId 订单编号
     * @return AlipayResultVO
     */
    AlipayResultVO getOrderStatus(String orderId);
}
