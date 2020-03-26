package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class SourceVO implements Serializable {

    private String sourceId;
    private String sourceName;
    private Boolean isActive;
}
