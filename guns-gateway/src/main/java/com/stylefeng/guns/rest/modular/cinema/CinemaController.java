package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.CinemaRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjxjwxk
 */
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceApi.class, check = false)
    private CinemaServiceApi cinemaServiceApi;

    @RequestMapping(value = "getCinemas")
    public ResponseVO getCinemas(CinemaRequestVO cinemaRequestVO) {

        // 按照5个条件进行筛选


        return null;
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
