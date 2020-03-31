package com.stylefeng.guns.api.cinema;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.vo.*;

import java.util.List;

/**
 * @author zjxjwxk
 */
public interface CinemaServiceApi {

    /**
     * 根据CinemaRequestVO，查询影院列表
     * @param cinemaRequestVO 包含5个属性的影院查询对象
     * @return CinemaVO的分页
     */
    Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO);

    /**
     * 根据条件获取品牌列表（除了99以外，其他的数字为isActive）
     * @param brandId 品牌编号
     * @return 品牌VO集合
     */
    List<BrandVO> getBrands(int brandId);

    /**
     * 获取行政区域列表
     * @param areaId 行政区域编号
     * @return 行政区域VO集合
     */
    List<AreaVO> getAreas(int areaId);

    /**
     * 获取影厅类型列表
     * @param hallType 影厅类型编号
     * @return 影厅类型VO集合
     */
    List<HallTypeVO> getHallTypes(int hallType);

    /**
     * 根据影院编号，获取影院信息
     * @param cinemaId 影院编号
     * @return 影院信息VO
     */
    CinemaInfoVO getCinemaInfo(int cinemaId);

    /**
     * 根据影院编号，获取影片信息
     * @param cinemaId 影院编号
     * @return 影片信息VO
     */
    FilmInfoVO getFilmInfoByCinemaId(int cinemaId);

    /**
     * 根据放映场次编号获取放映信息
     * @param fieldId 放映场次编号
     * @return 放映信息VO
     */
    FilmFieldVO getFilmField(int fieldId);

    /**
     * 根据放映场次编号查询对应的电影编号，然后根据电影编号获取对应的电影信息
     * @param fieldId 放映场次编号
     * @return 电影信息VO
     */
    FilmInfoVO getFilmInfoByFieldId(int fieldId);
}
