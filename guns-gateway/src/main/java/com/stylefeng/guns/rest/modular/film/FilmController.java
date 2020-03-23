package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zjxjwxk
 */
@RestController
@RequestMapping("/film/")
public class FilmController {

    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    /**
     * 获取首页信息接口
     * API网关：1. 功能聚合（API聚合）
     *            优点：1. 六个接口，一次请求，同一时刻节省了5次HTTP请求
     *                 2. 同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
 *                缺点：
     *                 1. 一次获取数据过多，容易出现问题
     * @return 响应VO
     */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {

        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        // 获取热映的影片
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true, 8));
        // 获取即将上映的影片
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 8));
        // 获取票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        // 获取人气榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        // 获取排行前100影片
        filmIndexVO.setTop100(filmServiceApi.getTop());
        return ResponseVO.success(filmIndexVO);
    }
}
