package com.lin.cms.demo.dto.user;

import com.lin.cms.demo.common.validator.EqualField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@NoArgsConstructor
@EqualField(srcField = "newPassword", dstField = "confirmPassword", message = "两次输入密码不一致")
public class ChangePasswordDTO {

    @NotBlank(message = "密码不可为空")
    @Pattern(regexp = "^[A-Za-z0-9_*&$#@]{6,22}$", message = "密码长度必须在6~22位之间，包含字符、数字和 _")
    private String newPassword;

    @NotBlank(message = "确认密码不可为空")
    private String confirmPassword;

    @NotBlank(message = "请输入旧密码")
    private String oldPassword;
}
