package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zjxjwxk
 */
@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "img.zjxjwxk.com/";

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
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true, 99, 99, 99, 99, 1, 8));
        // 获取即将上映的影片
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 99, 99, 99, 99, 1, 8));
        // 获取票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        // 获取人气榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        // 获取排行前100影片
        filmIndexVO.setTop100(filmServiceApi.getTop());
        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId) {
        FilmConditionVO filmConditionVO = new FilmConditionVO();

        // 类型集合
        List<CatVO> catVOList = filmServiceApi.getCats();
        List<CatVO> catResult = new ArrayList<>();
        // 标志符，用于标志集合是否存在catId
        boolean flag = false;
        CatVO cat = null;
        for (CatVO catVO : catVOList) {
            /* 找到全部（99）时，将其赋给cat，并跳过该次循环
               方便集合不存在catId的情况下，将其置为非active并加入catResult集合，而无需遍历第二次集合
               优化：【理论上】
                    1、数据层查询按Id进行排序【有序集合 -> 有序数组】
                    2、通过二分法查找
            */
            if ("99".equals(catVO.getCatId())) {
                cat = catVO;
                continue;
            }
            // 判断集合是否存在catId，如果存在，则将对应的实体变成active状态
            if (catVO.getCatId().equals(catId)) {
                flag = true;
                catVO.setIsActive(true);
            } else {
                catVO.setIsActive(false);
            }
            // 无论是不是active状态，都加入catResult集合
            catResult.add(catVO);
        }
        // 如果集合不存在catId，则默认将全部（99）变为active状态，并加入catResult集合
        if (!flag) {
            cat.setIsActive(true);
        } else {
            cat.setIsActive(false);
        }
        catResult.add(cat);

        // 片源集合
        List<SourceVO> sourceVOList = filmServiceApi.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        flag = false;
        SourceVO source = null;
        for (SourceVO sourceVO : sourceVOList) {
            if ("99".equals(sourceVO.getSourceId())) {
                source = sourceVO;
                continue;
            }
            if (sourceVO.getSourceId().equals(sourceId)) {
                flag = true;
                sourceVO.setIsActive(true);
            } else {
                sourceVO.setIsActive(false);
            }
            sourceResult.add(sourceVO);
        }
        if (!flag) {
            source.setIsActive(true);
        } else {
            source.setIsActive(false);
        }
        sourceResult.add(source);

        // 年代集合
        List<YearVO> yearVOList = filmServiceApi.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        flag = false;
        YearVO year = null;
        for (YearVO yearVO : yearVOList) {
            if ("99".equals(yearVO.getYearId())) {
                year = yearVO;
                continue;
            }
            if (yearVO.getYearId().equals(yearId)) {
                flag = true;
                yearVO.setIsActive(true);
            } else {
                yearVO.setIsActive(false);
            }
            yearResult.add(yearVO);
        }
        if (!flag) {
            year.setIsActive(true);
        } else {
            year.setIsActive(false);
        }
        yearResult.add(year);

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }


    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO) {

        FilmVO filmVO;
        filmRequestVO.init();
        // 根据showType判断影片查询类型
        switch (filmRequestVO.getShowType()) {
            case 1:
                filmVO = filmServiceApi.getHotFilms(false, filmRequestVO.getSortId(),
                        filmRequestVO.getCatId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getNowPage(), filmRequestVO.getPageSize());
                break;
            case 2:
                filmVO = filmServiceApi.getSoonFilms(false, filmRequestVO.getSortId(),
                        filmRequestVO.getCatId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getNowPage(), filmRequestVO.getPageSize());
                break;
            case 3:
                filmVO = filmServiceApi.getClassicFilms(filmRequestVO.getSortId(),
                        filmRequestVO.getCatId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getNowPage(), filmRequestVO.getPageSize());
                break;
            default:
                filmVO = filmServiceApi.getHotFilms(false, filmRequestVO.getSortId(),
                        filmRequestVO.getCatId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getNowPage(), filmRequestVO.getPageSize());
                break;
        }

        return ResponseVO.success(filmVO.getNowPage(), filmVO.getTotalPage(), IMG_PRE, filmVO);
    }

}
