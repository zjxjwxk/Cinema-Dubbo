package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AlipayServiceApi;
import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/order/")
public class OrderController {

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2020")
    private OrderServiceApi order2020ServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2019")
    private OrderServiceApi order2019ServiceApi;

    @Reference(interfaceClass = AlipayServiceApi.class, check = false)
    private AlipayServiceApi alipayServiceApi;

    /**
     * 令牌桶（限流算法）
     */
    private static TokenBucket tokenBucket = new TokenBucket();
    private static final String IMG_PRE = "http://img-cinema.zjxjwxk.com/";

    /**
     * 服务降级
     */
    public ResponseVO error(Integer fieldId, String soldSeats, String seatsName) {
        return ResponseVO.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }

    /**
     * Hystrix熔断器
     * 包含：
     * 信号量隔离
     * 线程池隔离
     * 线程切换
     */
//    @HystrixCommand(fallbackMethod = "error",
//            commandProperties = {
//                @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
//                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
//                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
//                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
//            },
//            threadPoolProperties = {
//                    @HystrixProperty(name = "coreSize", value = "1"),
//                    @HystrixProperty(name = "maxQueueSize", value = "10"),
//                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
//                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
//                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
//            })
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) {
        try {
            if (tokenBucket.getToken()) {
                // 获取当前用户的信息
                String userId = CurrentUser.getCurrentUser();
                if (userId == null || userId.trim().length() == 0) {
                    return ResponseVO.serviceFail("用户未登录");
                }
                // 验证所购买的票是否为真
                boolean isTrue = order2020ServiceApi.isTrueSeats(fieldId, soldSeats);
                // 检查所购买的票是否已售出
                boolean isSold = order2020ServiceApi.isSoldSeats(fieldId, soldSeats);

                // 验证上面两个条件，当所购买的票为真，且未售出时，才创建订单
                if (isTrue && !isSold) {
                    // 创建订单信息
                    OrderVO orderVO = order2020ServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                    if (orderVO == null) {
                        log.error("购票未成功");
                        return ResponseVO.serviceFail("购票未成功");
                    } else {
                        return ResponseVO.success(orderVO);
                    }
                } else {
                    return ResponseVO.serviceFail("订单中的座位编号不存在或已售出");
                }
            } else {
                return ResponseVO.serviceFail("购票人数过多");
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
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }
        // 获取当前用户的订单
        Page<OrderVO> page = new Page<>(nowPage, pageSize);
        Page<OrderVO> order2020VOPage = order2020ServiceApi.getOrderVOListByUserId(Integer.parseInt(userId), page);
        Page<OrderVO> order2019VOPage = order2019ServiceApi.getOrderVOListByUserId(Integer.parseInt(userId), page);

        // 分组聚合
        int totalPages = (int) (order2020VOPage.getPages() + order2019VOPage.getPages());
        List<OrderVO> orderVOList = new ArrayList<>();
        orderVOList.addAll(order2020VOPage.getRecords());
        orderVOList.addAll(order2019VOPage.getRecords());

        return ResponseVO.success(nowPage, totalPages, "", orderVOList);
    }

    @RequestMapping(value = "getPayInfo", method = RequestMethod.POST)
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId) {
        // 获取当前用户的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }

        // 设置隐式参数（将当前登录用户的编号userId传递给后端）
        RpcContext.getContext().setAttachment("userId", userId);

        // 获取订单二维码
        AlipayInfoVO alipayInfoVO = alipayServiceApi.getQRCode(orderId);
        if (alipayInfoVO == null) {
            return ResponseVO.serviceFail("订单支付失败，请稍后重试");
        } else {
            return ResponseVO.success(IMG_PRE, alipayInfoVO);
        }
    }

    @RequestMapping(value = "getPayResult", method = RequestMethod.POST)
    public ResponseVO getPayResult(@RequestParam("orderId") String orderId,
                                   @RequestParam(value = "tryNums", required = false, defaultValue = "1") Integer tryNums) {
        // 获取当前用户的信息
        String userId = CurrentUser.getCurrentUser();
        if (userId == null || userId.trim().length() == 0) {
            return ResponseVO.serviceFail("用户未登录");
        }

        // 设置隐式参数（将当前登录用户的编号userId传递给后端）
        RpcContext.getContext().setAttachment("userId", userId);

        // 判断是否支付超时
        if (tryNums > 3) {
            return ResponseVO.serviceFail("订单支付失败，请稍后重试");
        } else {
            AlipayResultVO alipayResultVO = alipayServiceApi.getOrderStatus(orderId);
            if (alipayResultVO == null || ToolUtil.isEmpty(alipayResultVO.getOrderId())) {
                AlipayResultVO serviceFailVO = new AlipayResultVO();
                serviceFailVO.setOrderId(orderId);
                serviceFailVO.setOrderStatus(0);
                serviceFailVO.setOrderMsg("支付失败");
                return ResponseVO.success(serviceFailVO);
            }
            return ResponseVO.success(alipayResultVO);
        }
    }
}
