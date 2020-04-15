package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;

/**
 * AlipayService本地伪装（服务降级）
 * @author zjxjwxk
 */
public class AlipayServiceMock implements AlipayServiceApi {
    @Override
    public AlipayInfoVO getQRCode(String orderId) {
        return null;
    }

    @Override
    public AlipayResultVO getOrderStatus(String orderId) {
        AlipayResultVO alipayResultVO = new AlipayResultVO();
        alipayResultVO.setOrderId(orderId);
        alipayResultVO.setOrderStatus(0);
        alipayResultVO.setOrderMsg("未支付成功");
        return alipayResultVO;
    }
}
