package com.lin.cms.demo.sleeve.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pedro
 * @since 2019-08-09
 */
@Data
public class SpuImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String img;

    private Long spuId;

    @TableLogic
    @JSONField(serialize = false)
    private Date deleteTime;

    @JSONField(serialize = false)
    private Date updateTime;

    @JSONField(serialize = false)
    private Date createTime;

    private Long themeId;

}
