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
 * @Description 借阅表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "borrow")
public class Borrow {

//    ID主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    用户ID
    private Integer userId;

//    图书ID
    private Integer bookId;

//    借阅时间
    private Date createTime;

//    归还时间
    private Date endTime;

//    实际归还时间
    private Date updateTime;

//    是否归还（0 已归还 1 未归还）
    private Integer ret;
}
