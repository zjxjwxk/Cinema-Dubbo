package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zjxjwxk
 */
@Data
public class FilmVO {

    private Integer filmNum;
    private List<FilmInfo> filmInfo;
}
