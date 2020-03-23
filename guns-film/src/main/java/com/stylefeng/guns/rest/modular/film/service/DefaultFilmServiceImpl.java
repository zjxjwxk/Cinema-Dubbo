package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.BannerVO;
import com.stylefeng.guns.api.film.vo.FilmInfo;
import com.stylefeng.guns.api.film.vo.FilmVO;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.BannerTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.FilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.BannerT;
import com.stylefeng.guns.rest.common.persistence.model.FilmT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Component
@Service(interfaceClass = FilmServiceApi.class)
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private BannerTMapper bannerTMapper;

    @Autowired
    private FilmTMapper filmTMapper;

    @Override
    public List<BannerVO> getBanners() {
        List<BannerVO> bannerVOList = new ArrayList<>();
        List<BannerT> bannerTList = bannerTMapper.selectList(null);
        for (BannerT bannerT : bannerTList) {
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(bannerT.getUuid() + "");
            bannerVO.setBannerAddress(bannerT.getBannerAddress());
            bannerVO.setBannerUrl(bannerT.getBannerUrl());
            bannerVOList.add(bannerVO);
        }
        return bannerVOList;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList = new ArrayList<>();
        // 热映影片的限制条件
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 如果是，则限制条数，限制内容为热映影片
            Page<FilmT> page = new Page<>(1, nums);
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);
            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setFilmInfo(filmInfoList);
        } else {
            // 如果不是，则是列表页，同样需要限制内容为热映影片

        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList = new ArrayList<>();
        // 即将上映影片的限制条件
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 如果是，则限制条数，限制内容为即将上映影片
            Page<FilmT> page = new Page<>(1, nums);
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);
            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setFilmInfo(filmInfoList);
        } else {
            // 如果不是，则是列表页，同样需要限制内容为即将上映影片

        }
        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        // 条件：正在上映的票房前十名
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        Page<FilmT> page = new Page<>(1, 10, "film_box_office");
        List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
        return getFilmInfoList(filmTList);
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        // 条件：即将上映的预售前十名
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        Page<FilmT> page = new Page<>(1, 10, "film_preSaleNum");
        List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
        return getFilmInfoList(filmTList);
    }

    @Override
    public List<FilmInfo> getTop() {
        // 条件：正在上映的评分前十名
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        Page<FilmT> page = new Page<>(1, 10, "film_score");
        List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
        return getFilmInfoList(filmTList);
    }

    private List<FilmInfo> getFilmInfoList(List<FilmT> filmTList) {
        List<FilmInfo> filmInfoList = new ArrayList<>();
        for (FilmT filmT : filmTList) {
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setFilmId(filmT.getUuid() + "");
            filmInfo.setFilmType(filmT.getFilmType());
            filmInfo.setImgAddress(filmT.getImgAddress());
            filmInfo.setFilmName(filmT.getFilmName());
            filmInfo.setFilmScore(filmT.getFilmScore());
            filmInfo.setExpectNum(filmT.getFilmPresalenum());
            filmInfo.setShowTime(DateUtil.getDay(filmT.getFilmTime()));
            filmInfo.setBoxNum(filmT.getFilmBoxOffice());
            filmInfo.setScore(filmT.getFilmScore());
            filmInfoList.add(filmInfo);
        }
        return filmInfoList;
    }
}
