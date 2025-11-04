package com.cya.util.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 分页返回
 */
@Data
public class PageOut {

//    当前页
    private Integer currPage;

//    每页条数
    private Integer pageSize;

//    总数
    private Integer total;

//    数据
    private Object list;
}
