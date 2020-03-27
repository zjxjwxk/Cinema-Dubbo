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
     * @param sortId 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
     * @param catId 类型编号
     * @param sourceId 区域编号
     * @param yearId 年代编号
     * @param nowPage 列表当前页
     * @param pageSize 每页显示条数（受限时）
     *
     * @return FilmVO
     */
    FilmVO getHotFilms(boolean isLimit, int sortId,
                       int catId, int sourceId, int yearId,
                       int nowPage, int pageSize);

    /**
     * 获取即将上映的影片（按照欢迎程度做排序）
     * @param isLimit 数量是否受限
     * @param sortId 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
     * @param catId 类型编号
     * @param sourceId 区域编号
     * @param yearId 年代编号
     * @param nowPage 列表当前页
     * @param pageSize 每页显示条数（受限时）
     * @return FilmVO
     */
    FilmVO getSoonFilms(boolean isLimit, int sortId,
                        int catId, int sourceId, int yearId,
                        int nowPage, int pageSize);

    /**
     * 获取经典影片
     * @param sortId 排序方式，1-按热门搜索，2-按时间搜索，3-按评价搜索
     * @param catId 类型编号
     * @param sourceId 区域编号
     * @param yearId 年代编号
     * @param nowPage 列表当前页
     * @param pageSize 每页显示条数（受限时）
     * @return FilmVO
     */
    FilmVO getClassicFilms(int sortId, int catId, int sourceId, int yearId,
                           int nowPage, int pageSize);

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
