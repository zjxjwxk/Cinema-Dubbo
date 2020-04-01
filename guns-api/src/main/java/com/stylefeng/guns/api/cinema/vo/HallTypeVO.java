package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class HallTypeVO implements Serializable {

    private String hallTypeId;
    private String hallTypeName;
    private Boolean isActive;
}
