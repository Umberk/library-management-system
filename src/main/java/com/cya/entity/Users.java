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
 * @Description 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "users")
public class Users {

//    ID主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    头像
    private String avatar;

//    昵称
    private String nickname;

//    用户名
    private String username;

//    密码
    private String password;

//    生日
    private Date birthday;

//    是否为管理员（0 管理员 1 普通用户）
    private Integer isAdmin;

//    电话
    private String tel;

//    邮箱
    private String email;

//    地址
    private String address;

//    可借数量
    private Integer size;

}
