package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldInfoResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaListResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zjxjwxk
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/cinema/")
public class CinemaController {

    private static final String IMG_PRE = "http://img-cinema.zjxjwxk.com/";

    @Reference(interfaceClass = CinemaServiceApi.class, cache = "lru", check = false)
    private CinemaServiceApi cinemaServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2020")
    private OrderServiceApi orderServiceApi;

    @RequestMapping(value = "getCinemas")
    public ResponseVO getCinemas(CinemaRequestVO cinemaRequestVO) {

        try {
            cinemaRequestVO.init();
            // 按照5个条件进行筛选
            Page<CinemaVO> cinemas = cinemaServiceApi.getCinemas(cinemaRequestVO);
            // 判断是否有满足条件的影院
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResponseVO.success("没有相关影院");
            } else {
                CinemaListResponseVO cinemaListResponseVO = new CinemaListResponseVO();
                cinemaListResponseVO.setCinemas(cinemas.getRecords());
                return ResponseVO.success(cinemas.getCurrent(), (int) cinemas.getPages(), "", cinemaListResponseVO.getCinemas());
            }
        } catch (Exception e) {
            // 异常处理
            log.error("查询影院列表失败", e);
            return ResponseVO.serviceFail("查询影院列表失败");
        }
    }

    /**
     * 热点数据（放缓存）
     */
    @RequestMapping(value = "getCondition")
    public ResponseVO getCondition(CinemaRequestVO cinemaRequestVO) {

        try {
            cinemaRequestVO.init();
            // 获取三个对象，然后封装成一个对象返回即可
            List<BrandVO> brandVOList = cinemaServiceApi.getBrands(cinemaRequestVO.getBrandId());
            List<AreaVO> areaVOList = cinemaServiceApi.getAreas(cinemaRequestVO.getAreaId());
            List<HallTypeVO> hallTypeVOList = cinemaServiceApi.getHallTypes(cinemaRequestVO.getHallType());
            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setBrandList(brandVOList);
            cinemaConditionResponseVO.setAreaList(areaVOList);
            cinemaConditionResponseVO.setHalltypeList(hallTypeVOList);
            return ResponseVO.success(cinemaConditionResponseVO);
        } catch (Exception e) {
            log.error("获取影院查询条件列表失败", e);
            return ResponseVO.serviceFail("获取影院查询条件列表失败");
        }
    }

    @RequestMapping(value = "getFields")
    public ResponseVO getFields(Integer cinemaId) {
        try {
            CinemaFieldsResponseVO cinemaFieldResponseVO = new CinemaFieldsResponseVO();
            CinemaInfoVO cinemaInfoVO = cinemaServiceApi.getCinemaInfo(cinemaId);
            List<FilmInfoVO> filmInfoVOList = cinemaServiceApi.getFilmListByCinemaId(cinemaId);
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoVO);
            cinemaFieldResponseVO.setFilmList(filmInfoVOList);
            return ResponseVO.success(IMG_PRE, cinemaFieldResponseVO);
        } catch (Exception e) {
            log.error("获取放映场次失败", e);
            return ResponseVO.serviceFail("获取放映场次失败");
        }
    }

    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId) {
        try {
            CinemaFieldInfoResponseVO cinemaFieldInfoResponseVO = new CinemaFieldInfoResponseVO();
            FilmInfoVO filmInfoVO = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
            CinemaInfoVO cinemaInfoVO = cinemaServiceApi.getCinemaInfo(cinemaId);
            HallInfoVO hallInfoVO = cinemaServiceApi.getFilmField(fieldId);
            hallInfoVO.setSoldSeats(orderServiceApi.getSoldSeatsByFieldId(fieldId));
            cinemaFieldInfoResponseVO.setFilmInfo(filmInfoVO);
            cinemaFieldInfoResponseVO.setCinemaInfo(cinemaInfoVO);
            cinemaFieldInfoResponseVO.setHallInfo(hallInfoVO);
            return ResponseVO.success(IMG_PRE, cinemaFieldInfoResponseVO);
        } catch (Exception e) {
            log.error("获取场次详细信息失败", e);
            return ResponseVO.serviceFail("获取场次详细信息失败");
        }
    }
}
