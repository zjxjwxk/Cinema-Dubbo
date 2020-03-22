package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Data
public class FilmVO implements Serializable {

    private Integer filmNum;
    private List<FilmInfo> filmInfo;
}
