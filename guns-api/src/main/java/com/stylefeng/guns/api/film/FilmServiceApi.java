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

    /**
     * 根据影片ID或者名称获取影片详情信息
     * @param searchType 0表示按照编号查找，1表示按照名称查找
     * @param searchParam 编号(searchType为1时)或名称(searchType为2时)
     * @return 影片详情VO
     */
    FilmDetailVO getFilmDetail(int searchType, String searchParam);

    /**
     * 获取影片描述信息
     * @param filmId film_t中的UUID
     * @return 影片描述VO
     */
    FilmDescVO getFilmDesc(String filmId);

    /**
     * 获取导演信息
     * @param filmId film_t中的UUID
     * @return 导演信息VO
     */
    ActorVO getDirectorInfo(String filmId);

    /**
     * 获取演员信息列表
     * @param filmId film_t中的UUID
     * @return 演员信息列表
     */
    List<ActorVO> getActors(String filmId);
}
