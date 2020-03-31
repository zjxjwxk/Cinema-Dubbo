package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class BrandVO implements Serializable {

    private String brandId;
    private String brandName;
    private Boolean isActive;
}
