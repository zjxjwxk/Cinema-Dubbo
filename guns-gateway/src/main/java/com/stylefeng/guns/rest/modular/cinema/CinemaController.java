package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.CinemaRequestVO;
import com.stylefeng.guns.api.cinema.vo.CinemaVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaListResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjxjwxk
 */
@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceApi.class, check = false)
    private CinemaServiceApi cinemaServiceApi;

    @RequestMapping(value = "getCinemas")
    public ResponseVO getCinemas(CinemaRequestVO cinemaRequestVO) {

        try {
            // 按照5个条件进行筛选
            Page<CinemaVO> cinemas = cinemaServiceApi.getCinemas(cinemaRequestVO);
            // 判断是否有满足条件的影院
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0) {
                return ResponseVO.success("没有相关影院");
            } else {
                CinemaListResponseVO cinemaListResponseVO = new CinemaListResponseVO();
                cinemaListResponseVO.setCinemas(cinemas.getRecords());
                return ResponseVO.success(cinemas.getCurrent(), (int) cinemas.getPages(), "", cinemaListResponseVO);
            }
        } catch (Exception e) {
            // 异常处理
            log.error("查询影院列表失败");
            e.printStackTrace();
            return ResponseVO.serviceFail("查询影院列表失败");
        }
    }

    @RequestMapping(value = "getCondition")
    public ResponseVO getCondition(CinemaRequestVO cinemaRequestVO) {

        return null;
    }

    @RequestMapping(value = "getFields", method = RequestMethod.POST)
    public ResponseVO getFields(Integer cinemaId) {

        return null;
    }

    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId) {

        return null;
    }
}
