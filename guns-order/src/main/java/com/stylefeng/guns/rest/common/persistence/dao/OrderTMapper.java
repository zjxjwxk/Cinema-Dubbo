package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.model.OrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author zjxjwxk
 * @since 2020-04-05
 */
public interface OrderTMapper extends BaseMapper<OrderT> {

    /**
     * 根据放映场次编号获取该场次的座位位置图的文件地址
     * @param fieldId 放映场次编号
     * @return 座位位置图的文件地址
     */
    String getSeatsByFieldId(@Param("fieldId") Integer fieldId);

    OrderVO getOrderVOById(@Param("orderId") String orderId);

    List<OrderVO> getOrderVOListByUserId(@Param("userId") Integer userId);

    String getSoldSeatsByFieldId(@Param("fieldId") Integer fieldId);
}
