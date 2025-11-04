package com.cya.util.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图书出参对象
 */
@Data
public class BookOut {

//    书籍ID
    private Integer id;

//    书籍ISBN编码
    private String isbn;

//    书名
    private String name;

//    作者
    private String author;

//    页数
    private Integer pages;

//    翻译
    private String translate;

//    出版社
    private String publish;

//    定价
    private Double price;

//    库存
    private Integer size;

//    分类
    private String type;

//    出版时间
    private String publishTime;
}
