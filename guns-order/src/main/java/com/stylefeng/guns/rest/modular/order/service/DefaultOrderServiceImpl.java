package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.dao.OrderTMapper;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zjxjwxk
 */
@Component
@Service(interfaceClass = OrderServiceApi.class)
public class DefaultOrderServiceImpl implements OrderServiceApi {

    @Autowired
    private OrderTMapper orderTMapper;

    @Autowired
    private FTPUtil ftpUtil;

    @Override
    public boolean isTrueSeats(Integer fieldId, String seats) {
        // 根据fieldId找到对应的座位位置图路径
        String seatAddress = orderTMapper.getSeatsByFieldId(fieldId);
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
        return false;
    }

    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        return null;
    }

    @Override
    public List<OrderVO> getOrderVOListByUserId(Integer userId) {
        return null;
    }

    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        return null;
    }
}
