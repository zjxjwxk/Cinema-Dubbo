package com.stylefeng.guns.rest.modular.cinema.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.CinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.model.CinemaT;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
public class DefaultCinemaServiceImpl implements CinemaServiceApi {

    @Autowired
    private CinemaTMapper cinemaTMapper;

    @Override
    public Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO) {

        cinemaRequestVO.init();
        List<CinemaVO> cinemaVOList = new ArrayList<>();
        Page<CinemaT> page = new Page<>(cinemaRequestVO.getNowPage(), cinemaRequestVO.getPageSize());
        EntityWrapper<CinemaT> entityWrapper = new EntityWrapper<>();

        // 判断是否传入查询条件：brandId，hallType，districtId 是否为99（表示全部）
        if (cinemaRequestVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaRequestVO.getBrandId());
        }
        if (cinemaRequestVO.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%#" + cinemaRequestVO.getHallType() + "#%");
        }
        if (cinemaRequestVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaRequestVO.getDistrictId());
        }

        List<CinemaT> cinemaTList = cinemaTMapper.selectPage(page, entityWrapper);

        // 将数据实体转换为业务实体
        for (CinemaT cinemaT : cinemaTList) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(cinemaT.getUuid() + "");
            cinemaVO.setCinemaName(cinemaT.getCinemaName());
            cinemaVO.setAddress(cinemaT.getCinemaAddress());
            cinemaVO.setMinimumPrice(cinemaT.getMinimumPrice() + "");
            cinemaVOList.add(cinemaVO);
        }

        // 根据条件，判断影院列表总数
        long counts = cinemaTMapper.selectCount(entityWrapper);

        // 组织返回对象
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemaVOList);
        result.setSize(cinemaRequestVO.getPageSize());
        result.setTotal(counts);

        return result;
    }

    @Override
    public List<BrandVO> getBrands(int brandId) {
        return null;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        return null;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        return null;
    }

    @Override
    public CinemaInfoVO getCinemaInfo(int cinemaId) {
        return null;
    }

    @Override
    public FilmInfoVO getFilmInfoByCinemaId(int cinemaId) {
        return null;
    }

    @Override
    public FilmFieldVO getFilmField(int fieldId) {
        return null;
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        return null;
    }
}
