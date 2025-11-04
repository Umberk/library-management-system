package com.cya.util.vo;

import com.cya.entity.Users;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 用户vo类
 */
@Data
public class UserOut extends Users {

//    身份
    private String ident;

//    生日：yyyy-MM-dd格式
    private String birth;
}
