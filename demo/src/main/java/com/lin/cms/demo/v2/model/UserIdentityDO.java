package com.lin.cms.demo.v2.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pedro
 * @since 2019-12-02
 */
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("lin_user_identity")
public class UserIdentityDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 认证类型，例如 username_password，用户名-密码认证
     */
    private String identityType;

    /**
     * 认证，例如 用户名
     */
    private String identifier;

    /**
     * 凭证，例如 密码
     */
    private String credential;

    @JsonIgnore
    @JSONField(serialize = false)
    private Date createTime;

    @JsonIgnore
    @JSONField(serialize = false)
    private Date updateTime;

    @JSONField(serialize = false)
    @TableLogic
    private Date deleteTime;
}
