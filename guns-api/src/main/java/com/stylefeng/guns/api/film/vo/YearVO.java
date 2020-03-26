package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class YearVO implements Serializable {

    private String yearId;
    private String yearName;
    private Boolean isActive;
}
