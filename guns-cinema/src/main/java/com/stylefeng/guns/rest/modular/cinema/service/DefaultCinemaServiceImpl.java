package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
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
@Service(interfaceClass = CinemaServiceApi.class)
public class DefaultCinemaServiceImpl implements CinemaServiceApi {

    @Autowired
    private CinemaTMapper cinemaTMapper;

    @Autowired
    private BrandDictTMapper brandDictTMapper;

    @Autowired
    private AreaDictTMapper areaDictTMapper;

    @Autowired
    private HallDictTMapper hallDictTMapper;

    @Autowired
    private FieldTMapper fieldTMapper;

    @Override
    public Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO) {

        List<CinemaVO> cinemaVOList = new ArrayList<>();
        Page<CinemaT> page = new Page<>(cinemaRequestVO.getNowPage(), cinemaRequestVO.getPageSize());
        EntityWrapper<CinemaT> entityWrapper = new EntityWrapper<>();

        // 判断是否传入查询条件：brandId，hallType，areaId 是否为99（表示全部）
        if (cinemaRequestVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaRequestVO.getBrandId());
        }
        if (cinemaRequestVO.getHallType() != 99) {
            entityWrapper.like("hall_ids", "%#" + cinemaRequestVO.getHallType() + "#%");
        }
        if (cinemaRequestVO.getAreaId() != 99) {
            entityWrapper.eq("area_id", cinemaRequestVO.getAreaId());
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
        boolean flag = false;
        List<BrandVO> brandVOList = new ArrayList<>();
        BrandDictT brandDictT = brandDictTMapper.selectById(brandId);
        // 判断传入的id是否存在，或是否为99
        if (brandId == 99 || brandDictT == null || brandDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<BrandDictT> brandDictTList = brandDictTMapper.selectList(null);
        for (BrandDictT brand : brandDictTList) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(brand.getUuid() + "");
            brandVO.setBrandName(brand.getShowName());
            // 如果flag == true，则所有的isActive都置为true；否则，仅将编号为brandId的isActive置为true
            if (flag) {
                if (brand.getUuid() == 99) {
                    brandVO.setIsActive(true);
                } else {
                    brandVO.setIsActive(false);
                }
            } else {
                if (brand.getUuid() == brandId) {
                    brandVO.setIsActive(true);
                } else {
                    brandVO.setIsActive(false);
                }
            }
            brandVOList.add(brandVO);
        }
        return brandVOList;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOList = new ArrayList<>();
        AreaDictT areaDictT = areaDictTMapper.selectById(areaId);
        // 判断传入的id是否存在，或是否为99
        if (areaId == 99 || areaDictT == null || areaDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<AreaDictT> areaDictTList = areaDictTMapper.selectList(null);
        for (AreaDictT area : areaDictTList) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaId(area.getUuid() + "");
            areaVO.setAreaName(area.getShowName());
            // 如果flag == true，则所有的isActive都置为true；否则，仅将编号为areaId的isActive置为true
            if (flag) {
                if (area.getUuid() == 99) {
                    areaVO.setIsActive(true);
                } else {
                    areaVO.setIsActive(false);
                }
            } else {
                if (area.getUuid() == areaId) {
                    areaVO.setIsActive(true);
                } else {
                    areaVO.setIsActive(false);
                }
            }
            areaVOList.add(areaVO);
        }
        return areaVOList;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOList = new ArrayList<>();
        HallDictT hallDictT = hallDictTMapper.selectById(hallType);
        // 判断传入的id是否存在，或是否为99
        if (hallType == 99 || hallDictT == null || hallDictT.getUuid() == null) {
            flag = true;
        }
        // 查询所有列表
        List<HallDictT> hallDictTList = hallDictTMapper.selectList(null);
        for (HallDictT hall : hallDictTList) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHallTypeId(hall.getUuid() + "");
            hallTypeVO.setHallTypeName(hall.getShowName());
            // 如果flag == true，则所有的isActive都置为true；否则，仅将编号为hallTypeId的isActive置为true
            if (flag) {
                if (hall.getUuid() == 99) {
                    hallTypeVO.setIsActive(true);
                } else {
                    hallTypeVO.setIsActive(false);
                }
            } else {
                if (hall.getUuid() == hallType) {
                    hallTypeVO.setIsActive(true);
                } else {
                    hallTypeVO.setIsActive(false);
                }
            }
            hallTypeVOList.add(hallTypeVO);
        }
        return hallTypeVOList;
    }

    @Override
    public CinemaInfoVO getCinemaInfo(int cinemaId) {
        CinemaT cinemaT = cinemaTMapper.selectById(cinemaId);

        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setCinemaId(cinemaT.getUuid() + "");
        cinemaInfoVO.setImgUrl(cinemaT.getImgAddress());
        cinemaInfoVO.setCinemaName(cinemaT.getCinemaName());
        cinemaInfoVO.setCinemaAddress(cinemaT.getCinemaAddress());
        cinemaInfoVO.setCinemaPhone(cinemaT.getCinemaPhone());
        return cinemaInfoVO;
    }

    @Override
    public List<FilmInfoVO> getFilmListByCinemaId(int cinemaId) {
        return fieldTMapper.getFilmInfoListByCinemaId(cinemaId);
    }

    @Override
    public HallInfoVO getFilmField(int fieldId) {
        return fieldTMapper.getHallInfo(fieldId);
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        return fieldTMapper.getFilmInfoById(fieldId);
    }

    @Override
    public OrderQueryVO getOrderQueryVOByFieldId(int fieldId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        FieldT fieldT = fieldTMapper.selectById(fieldId);
        orderQueryVO.setCinemaId(fieldT.getCinemaId() + "");
        orderQueryVO.setFilmId(fieldT.getFilmId() + "");
        orderQueryVO.setFilmPrice(fieldT.getPrice() + "");
        return orderQueryVO;
    }
}
