package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;

/**
 * @author zjxjwxk
 */
@Data
public class FilmRequestVO {

    private Integer showType;
    private Integer sortId;
    private Integer catId;
    private Integer sourceId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;

    public void init() {
        if (this.showType == null) {
            this.showType = 1;
        }
        if (this.sortId == null) {
            this.sortId = 1;
        }
        if (this.catId == null) {
            this.catId = 99;
        }
        if (this.sourceId == null) {
            this.sourceId = 99;
        }
        if (this.yearId == null) {
            this.yearId = 99;
        }
        if (this.nowPage == null) {
            this.nowPage = 1;
        }
        if (this.pageSize == null) {
            this.pageSize = 18;
        }
    }
}
