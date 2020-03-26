package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

/**
 * @author zjxjwxk
 */
public interface FilmServiceApi {

    /**
     * 获取banners
     * @return BannerVO
     */
    List<BannerVO> getBanners();

    /**
     * 获取热映影片
     * @param isLimit 数量是否受限
     * @param nums 数量（受限时）
     * @return FilmVO
     */
    FilmVO getHotFilms(boolean isLimit, int nums);

    /**
     * 获取即将上映的影片（按照欢迎程度做排序）
     * @param isLimit 数量是否受限
     * @param nums 数量（受限时）
     * @return FilmVO
     */
    FilmVO getSoonFilms(boolean isLimit, int nums);

    /**
     * 获取票房排行榜
     * @return FilmInfo集合
     */
    List<FilmInfo> getBoxRanking();

    /**
     * 获取人气排行榜
     * @return FilmInfo集合
     */
    List<FilmInfo> getExpectRanking();

    /**
     * 获取top100
     * @return FilmInfo集合
     */
    List<FilmInfo> getTop();

    // 获取影片条件接口

    /**
     * 分类条件
     * @return CatVO集合
     */
    List<CatVO> getCats();

    /**
     * 片源条件
     * @return SourceVO集合
     */
    List<SourceVO> getSources();

    /**
     * 年代条件
     * @return YearVO集合
     */
    List<YearVO> getYears();
}
