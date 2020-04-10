package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.Order2018TMapper;
import com.stylefeng.guns.rest.common.persistence.model.Order2018T;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class, group = "order2018")
public class DefaultOrder2018ServiceImpl implements OrderServiceApi {

    @Autowired
    private Order2018TMapper order2018TMapper;

    @Reference(interfaceClass = CinemaServiceApi.class, check = false)
    private CinemaServiceApi cinemaServiceApi;

    @Autowired
    private FTPUtil ftpUtil;

    @Override
    public boolean isTrueSeats(Integer fieldId, String seats) {
        // 根据fieldId找到对应的座位位置图路径
        String seatAddress = order2018TMapper.getSeatsByFieldId(fieldId);
        // 根据路径读取位置图，以判断seats是否为真
        String fileStr = ftpUtil.getFileStrByAddress(seatAddress);

        // 将fileStr转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStr);
        // seats=1,2,3  ids="1,2,3,4,5"
        String ids = jsonObject.get("ids").toString();
        String[] seatArr = seats.split(",");
        String[] idArr = ids.split(",");
        int matchCnt = 0;
        for (String seat : seatArr) {
            for (String id : idArr) {
                if (seat.equalsIgnoreCase(id)) {
                    matchCnt++;
                }
            }
        }
        return seatArr.length == matchCnt;
    }

    @Override
    public boolean isSoldSeats(Integer fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);
        List<Order2018T> order2018TListTList = order2018TMapper.selectList(entityWrapper);
        String[] seatArr = seats.split(",");
        // 遍历该放映场次的所有订单，如果存在某一订单的座位包含seats中的座位，则表示已售出
        for (Order2018T order2018T : order2018TListTList) {
            String[] ids = order2018T.getSeatsIds().split(",");
            for (String seat : seatArr) {
                for (String id : ids) {
                    if (seat.equalsIgnoreCase(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        // 生成订单编号
        String uuid = UUIDUtil.genUuid();
        OrderQueryVO orderQueryVO = cinemaServiceApi.getOrderQueryVOByFieldId(fieldId);
        // 影院信息
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        // 影片信息
        Integer filmId = Integer.parseInt(orderQueryVO.getFilmId());
        Double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());
        // 计算订单总金额
        int seatsCnt = soldSeats.split(",").length;
        Double orderPrice = getOrderPrice(seatsCnt, filmPrice);

        Order2018T order2018T = new Order2018T();
        order2018T.setUuid(uuid);
        order2018T.setCinemaId(cinemaId);
        order2018T.setFieldId(fieldId);
        order2018T.setFilmId(filmId);
        order2018T.setFilmPrice(filmPrice);
        order2018T.setSeatsIds(soldSeats);
        order2018T.setSeatsName(seatsName);
        order2018T.setOrderPrice(orderPrice);
        order2018T.setOrderUser(userId);
        Integer insert = order2018TMapper.insert(order2018T);
        if (insert > 0) {
            OrderVO orderVO = order2018TMapper.getOrderVOById(uuid);
            if (orderVO == null || orderVO.getOrderId() == null) {
                log.error("订单信息查询失败，订单编号为:{}", uuid);
                return null;
            } else {
                return orderVO;
            }
        } else {
            log.error("订单插入失败，订单编号为:{}", uuid);
        }
        return null;
    }

    @Override
    public Page<OrderVO> getOrderVOListByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if (userId == null) {
            log.error("用户订单查询失败，用户编号未传入");
            return null;
        } else {
            List<OrderVO> orderVOList = order2018TMapper.getOrderVOListByUserId(userId, page);
            if (orderVOList == null || orderVOList.size() == 0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                // 获取订单总数
                EntityWrapper<Order2018T> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user", userId);
                Integer count = order2018TMapper.selectCount(entityWrapper);
                // 将结果放入Page
                result.setTotal(count);
                result.setRecords(orderVOList);
                return result;
            }
        }
    }

    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId == null) {
            log.error("获取已售座位失败，放映场次编号未传入");
            return "";
        } else {
            return order2018TMapper.getSoldSeatsByFieldId(fieldId);
        }
    }

    private Double getOrderPrice(int seatsCnt, double filmPrice) {
        BigDecimal seatsCntDecimal = new BigDecimal(seatsCnt);
        BigDecimal filmPriceDecimal = new BigDecimal(filmPrice);
        BigDecimal orderPrice = seatsCntDecimal.multiply(filmPriceDecimal);
        // 保留两位小数（四舍五入）
        BigDecimal result = orderPrice.setScale(2, RoundingMode.HALF_UP);
        return result.doubleValue();
    }
}