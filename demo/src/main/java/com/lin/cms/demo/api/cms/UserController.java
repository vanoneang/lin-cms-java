package com.lin.cms.demo.api.cms;

import com.lin.cms.core.annotation.AdminRequired;
import com.lin.cms.core.annotation.LoginRequired;
import com.lin.cms.core.annotation.RefreshRequired;
import com.lin.cms.core.annotation.RouteMeta;
import com.lin.cms.exception.Forbidden;
import com.lin.cms.exception.HttpException;
import com.lin.cms.exception.NotFound;
import com.lin.cms.exception.Parameter;
import com.lin.cms.core.result.Result;
import com.lin.cms.demo.model.UserDO;
import com.lin.cms.demo.utils.LocalUser;
import com.lin.cms.demo.vo.UserWithAuthsVO;
import com.lin.cms.utils.ResultGenerator;
import com.lin.cms.demo.service.UserService;
import com.lin.cms.token.JWT;
import com.lin.cms.demo.dto.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by lin on 2019/05/23.
 * License MIT
 */

@RestController
@RequestMapping("/cms/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWT jwt;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @AdminRequired
    public Result<String> register(@RequestBody @Valid RegisterDTO validator) throws Forbidden {
        userService.createUser(validator);
        return ResultGenerator.genSuccessResult("添加用户成功！");
    }

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public Map login(@RequestBody @Valid LoginDTO validator) throws HttpException {
        UserDO user = userService.findByNickname(validator.getNickname());
        if (user == null) {
            throw new NotFound("未找到相关用户");
        }
        Map res = jwt.generateTokens(user.getId());
        return res;
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @LoginRequired
    public Result update(@RequestBody @Valid UpdateInfoDTO validator) throws Parameter {
        userService.updateUser(validator);
        return ResultGenerator.genSuccessResult("更新成功！");
    }

    /**
     * 修改密码
     */
    @PutMapping("/change_password")
    @LoginRequired
    public Result updatePassword(@RequestBody @Valid ChangePasswordDTO validator) throws Parameter {
        userService.changePassword(validator);
        return ResultGenerator.genSuccessResult("密码修改成功！");
    }

    /**
     * 刷新令牌
     */
    @GetMapping("/refresh")
    @RefreshRequired
    public Map refreshToken() {
        UserDO user = LocalUser.getLocalUser();
        Map res = jwt.generateTokens(user.getId());
        return res;
    }

    /**
     * 查询拥有权限
     */
    @GetMapping("/auths")
    @LoginRequired
    @RouteMeta(auth = "查询自己拥有的权限", module = "用户", mount = true)
    public UserWithAuthsVO getAuths() {
        UserDO user = LocalUser.getLocalUser();
        if (user.checkAdmin()) {
            return new UserWithAuthsVO(user);
        }
        List<Map<String, List<Map<String, String>>>> auths = userService.getAuths(user.getGroupId());
        return new UserWithAuthsVO(user, auths);
    }

    /**
     * 查询自己信息
     */
    @LoginRequired
    @RouteMeta(auth = "查询自己信息", module = "用户", mount = true)
    @GetMapping("/information")
    public UserDO getInformation() {
        UserDO user = LocalUser.getLocalUser();
        return user;
    }

    /**
     * 修改头像
     */
    @LoginRequired
    @PutMapping("/avatar")
    public Result updateAvatar(@RequestBody @Valid AvatarUpdateDTO validator) {
        userService.updateAvatar(validator);
        return ResultGenerator.genSuccessResult("头像更新成功！");
    }
}
