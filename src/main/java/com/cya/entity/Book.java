package com.cya.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 图书实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "book")
public class Book {

//    ID主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    图书ISBN编码
    private String isbn;

//    图书名称
    private String name;

//    图书作者
    private String author;

//    图书页数
    private Integer pages;

//    翻译
    private String translate;

//    出版社
    private String publish;

//    单价
    private Double price;

//    库存
    private Integer size;

//    分类
    private String type;

//    出版时间
    private Date publishTime;

}
