package com.cya.util.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 归还 vo对象
 */
@Data
public class BackOut extends BookOut{

//    借阅时间
    private String borrowTime;

//    应还时间
    private String endTime;

//    是否逾期
    private String late;

}
