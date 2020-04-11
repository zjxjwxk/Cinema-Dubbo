package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    /**
     * 令牌桶（限流算法）
     */
    private static TokenBucket tokenBucket = new TokenBucket();

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2018")
    private OrderServiceApi order2018ServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2017")
    private OrderServiceApi order2017ServiceApi;

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
    @HystrixCommand(fallbackMethod = "error",
            commandProperties = {
                @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
                @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
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
                boolean isTrue = order2018ServiceApi.isTrueSeats(fieldId, soldSeats);
                // 检查所购买的票是否已售出
                boolean isSold = order2018ServiceApi.isSoldSeats(fieldId, soldSeats);

                // 验证上面两个条件，当所购买的票为真，且未售出时，才创建订单
                if (isTrue && !isSold) {
                    // 创建订单信息
                    OrderVO orderVO = order2018ServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
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
        Page<OrderVO> order2018VOPage = order2018ServiceApi.getOrderVOListByUserId(Integer.parseInt(userId), page);
        Page<OrderVO> order2017VOPage = order2017ServiceApi.getOrderVOListByUserId(Integer.parseInt(userId), page);

        // 分组聚合
        int totalPages = (int) (order2018VOPage.getPages() + order2017VOPage.getPages());
        List<OrderVO> orderVOList = new ArrayList<>();
        orderVOList.addAll(order2018VOPage.getRecords());
        orderVOList.addAll(order2017VOPage.getRecords());

        return ResponseVO.success(nowPage, totalPages, "", orderVOList);
    }

    @RequestMapping(value = "getPayInfo", method = RequestMethod.POST)
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId) {

        return null;
    }

    @RequestMapping(value = "getPayResult", method = RequestMethod.POST)
    public ResponseVO getPayResult(@RequestParam("orderId") String orderId, @RequestParam("tryNums") Integer tryNums) {

        return null;
    }
}
