package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class CinemaRequestVO implements Serializable {

    private Integer brandId;
    private Integer hallType;
    private Integer areaId;
    private Integer pageSize;
    private Integer nowPage;

    /**
     * 初始化为默认值，需要手动调用避免一些问题
     */
    public void init() {
        if (this.brandId == null) {
            this.brandId = 99;
        }
        if (this.hallType == null) {
            this.hallType = 99;
        }
        if (this.areaId == null) {
            this.areaId = 99;
        }
        if (this.pageSize == null) {
            this.pageSize = 12;
        }
        if (this.nowPage == null) {
            this.nowPage = 1;
        }
    }
}
