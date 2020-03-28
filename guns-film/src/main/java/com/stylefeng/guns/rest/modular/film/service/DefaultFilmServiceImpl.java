package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
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

    @Autowired
    private CatDictTMapper catDictTMapper;

    @Autowired
    private SourceDictTMapper sourceDictTMapper;

    @Autowired
    private YearDictTMapper yearDictTMapper;

    @Autowired
    private FilmInfoTMapper filmInfoTMapper;

    @Autowired
    private ActorTMapper actorTMapper;

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
    public FilmVO getHotFilms(boolean isLimit, int sortId,
                              int catId, int sourceId, int yearId,
                              int nowPage, int pageSize) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList;
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        // 热映影片的限制条件
        entityWrapper.eq("film_status", "1");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 如果是首页，则限制条数
            Page<FilmT> page = new Page<>(1, pageSize);
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);

            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setFilmInfo(filmInfoList);
        } else {
            // 如果不是首页，即是列表页，则需要增加sortId、catId、sourceId、yearId、nowPage限制查询条件和分页

            // 根据sortId的不同，来组织不同的Page对象
            Page<FilmT> page;
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, pageSize, "film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage, pageSize, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, pageSize, "film_score");
                    break;
                default:
                    page = new Page<>(nowPage, pageSize, "film_box_office");
                    break;
            }

            // 如果catId，sourceId，yearId不为99，则表示要按照对应的编号进行查询
            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);

            // 计算总页数
            int totalCount = filmTMapper.selectCount(entityWrapper);
            int totalPage = (totalCount / pageSize) + 1;

            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);

            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPage);
            filmVO.setFilmInfo(filmInfoList);
        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int sortId, int catId, int sourceId, int yearId, int nowPage, int pageSize) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList;
        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        // 即将上映影片的限制条件
        entityWrapper.eq("film_status", "2");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 如果是，则限制条数，限制内容为即将上映影片
            Page<FilmT> page = new Page<>(1, pageSize);
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);
            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);

            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setFilmInfo(filmInfoList);
        } else {
            // 如果不是，则是列表页，同样需要限制内容为即将上映影片

            // 根据sortId的不同，来组织不同的Page对象
            Page<FilmT> page;
            switch (sortId) {
                case 1:
                    page = new Page<>(nowPage, pageSize, "film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage, pageSize, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, pageSize, "film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage, pageSize, "film_preSaleNum");
                    break;
            }

            // 如果catId，sourceId，yearId不为99，则表示要按照对应的编号进行查询
            if (catId != 99) {
                String catStr = "%#" + catId + "#%";
                entityWrapper.like("film_cats", catStr);
            }
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }
            if (yearId != 99) {
                entityWrapper.eq("film_date", yearId);
            }
            List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);

            // 计算总页数
            int totalCount = filmTMapper.selectCount(entityWrapper);
            int totalPage = (totalCount / pageSize) + 1;

            // 组织filmInfo
            filmInfoList = getFilmInfoList(filmTList);

            filmVO.setFilmNum(filmInfoList.size());
            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPage);
            filmVO.setFilmInfo(filmInfoList);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int sortId, int catId, int sourceId, int yearId, int nowPage, int pageSize) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> filmInfoList;

        EntityWrapper<FilmT> entityWrapper = new EntityWrapper<>();
        // 即将上映影片的限制条件
        entityWrapper.eq("film_status", "3");

        // 根据sortId的不同，来组织不同的Page对象
        Page<FilmT> page;
        switch (sortId) {
            case 1:
                page = new Page<>(nowPage, pageSize, "film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage, pageSize, "film_time");
                break;
            case 3:
                page = new Page<>(nowPage, pageSize, "film_score");
                break;
            default:
                page = new Page<>(nowPage, pageSize, "film_box_office");
                break;
        }

        // 如果catId，sourceId，yearId不为99，则表示要按照对应的编号进行查询
        if (catId != 99) {
            String catStr = "%#" + catId + "#%";
            entityWrapper.like("film_cats", catStr);
        }
        if (sourceId != 99) {
            entityWrapper.eq("film_source", sourceId);
        }
        if (yearId != 99) {
            entityWrapper.eq("film_date", yearId);
        }
        List<FilmT> filmTList = filmTMapper.selectPage(page, entityWrapper);

        // 计算总页数
        int totalCount = filmTMapper.selectCount(entityWrapper);
        int totalPage = (totalCount / pageSize) + 1;

        // 组织filmInfo
        filmInfoList = getFilmInfoList(filmTList);

        filmVO.setFilmNum(filmInfoList.size());
        filmVO.setNowPage(nowPage);
        filmVO.setTotalPage(totalPage);
        filmVO.setFilmInfo(filmInfoList);
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

    @Override
    public List<CatVO> getCats() {
        List<CatVO> catVOList = new ArrayList<>();
        // 查询实体对象CatDictT
        List<CatDictT> catDictTList = catDictTMapper.selectList(null);

        // 将实体对象转换为业务对象CatVO
        for (CatDictT catDictT : catDictTList) {
            CatVO catVO = new CatVO();
            catVO.setCatId(catDictT.getUuid() + "");
            catVO.setCatName(catDictT.getShowName());
            catVOList.add(catVO);
        }
        return catVOList;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sourceVOList = new ArrayList<>();
        // 查询实体对象SourceDictT
        List<SourceDictT> sourceDictTList = sourceDictTMapper.selectList(null);

        // 将实体对象转换为业务对象SourceVO
        for (SourceDictT sourceDictT : sourceDictTList) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(sourceDictT.getUuid() + "");
            sourceVO.setSourceName(sourceDictT.getShowName());
            sourceVOList.add(sourceVO);
        }
        return sourceVOList;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> yearVOList = new ArrayList<>();
        // 查询实体对象YearDictT
        List<YearDictT> yearDictTList = yearDictTMapper.selectList(null);

        // 将实体对象转换为业务对象YearVO
        for (YearDictT yearDictT : yearDictTList) {
            YearVO yearVO = new YearVO();
            yearVO.setYearId(yearDictT.getUuid() + "");
            yearVO.setYearName(yearDictT.getShowName());
            yearVOList.add(yearVO);
        }
        return yearVOList;
    }

    @Override
    public FilmDetailVO getFilmDetail(int searchType, String searchParam) {
        FilmDetailVO filmDetailVO;
        // searchType：0表示按照编号查找，1表示按照名称查找
        if (searchType == 0) {
            filmDetailVO = filmTMapper.getFilmDetailById(searchParam);
        } else {
            filmDetailVO = filmTMapper.getFilmDetailByName(searchParam);
        }
        if (filmDetailVO != null) {
            filmDetailVO.setImgs(getImgs(filmDetailVO.getFilmId()));
        }
        return filmDetailVO;
    }

    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        FilmInfoT filmInfoT = getFilmInfo(filmId);
        FilmDescVO filmDescVO = new FilmDescVO();

        filmDescVO.setBiography(filmInfoT.getBiography());
        return filmDescVO;
    }

    @Override
    public ActorVO getDirectorInfo(String filmId) {
        FilmInfoT filmInfoT = getFilmInfo(filmId);

        // 获取导演编号
        Integer directorId = filmInfoT.getDirectorId();
        ActorT actorT = actorTMapper.selectById(directorId);

        ActorVO actorVO = new ActorVO();
        actorVO.setImgAddress(actorT.getActorImg());
        actorVO.setDirectorName(actorT.getActorName());
        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmId) {
        return actorTMapper.getActors(filmId);
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

    /**
     * 获取图片信息
     * @param filmId 影片UUID
     * @return ImgVO
     */
    private ImgVO getImgs(String filmId) {
        FilmInfoT filmInfoT = getFilmInfo(filmId);
        // 图片地址是5个以','为分隔符的链接url
        String filmImgsStr = filmInfoT.getFilmImgs();
        String[] filmImgs = filmImgsStr.split(",");

        ImgVO imgVO = new ImgVO();
        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg02(filmImgs[2]);
        imgVO.setImg03(filmImgs[3]);
        imgVO.setImg04(filmImgs[4]);
        return imgVO;
    }

    private FilmInfoT getFilmInfo(String filmId) {
        FilmInfoT filmInfoT = new FilmInfoT();
        filmInfoT.setFilmId(filmId);

        filmInfoT = filmInfoTMapper.selectOne(filmInfoT);
        return filmInfoT;
    }
}
