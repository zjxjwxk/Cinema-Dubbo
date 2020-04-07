package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zjxjwxk
 */
@Data
public class OrderQueryVO implements Serializable {

    private String cinemaId;
    private String filmId;
    private String filmPrice;
}
