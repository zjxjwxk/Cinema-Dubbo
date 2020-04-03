package com.stylefeng.guns.rest.modular.cinema.vo;

import com.stylefeng.guns.api.cinema.vo.CinemaInfoVO;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.HallInfoVO;
import lombok.Data;

/**
 * @author zjxjwxk
 */
@Data
public class CinemaFieldInfoResponseVO {

    private FilmInfoVO filmInfo;
    private CinemaInfoVO cinemaInfo;
    private HallInfoVO hallInfo;
}
