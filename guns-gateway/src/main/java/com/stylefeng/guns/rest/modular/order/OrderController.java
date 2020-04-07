package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjxjwxk
 */
@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    @Reference(interfaceClass = OrderServiceApi.class, check = false)
    private OrderServiceApi orderServiceApi;

    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        try {
            // 验证所购买的票是否为真
            boolean isTrue = orderServiceApi.isTrueSeats(fieldId, soldSeats);
            // 检查所购买的票是否已售出
            boolean isSold = orderServiceApi.isSoldSeats(fieldId, soldSeats);

            // 验证上面两个条件，当所购买的票为真，且未售出时，才创建订单
            if (isTrue && !isSold) {
                // 创建订单信息
                String userId = CurrentUser.getCurrentUser();
                OrderVO orderVO = orderServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                if (orderVO == null) {
                    log.error("购票未成功");
                    return ResponseVO.serviceFail("购票未成功");
                } else {
                    return ResponseVO.success(orderVO);
                }
            } else {
                return ResponseVO.serviceFail("订单中的座位编号不存在或已售出");
            }
        } catch (Exception e) {
            log.error("购票业务异常", e);
            return ResponseVO.serviceFail("购票业务异常");
        }
    }

    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
                                   @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize) {
        // 获取当前用户的信息

        // 获取当前用户的订单

        return null;
    }
}
