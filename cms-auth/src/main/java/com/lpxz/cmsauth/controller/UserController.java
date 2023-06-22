package com.lpxz.cmsauth.controller;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.lpxz.cmsauth.constant.UserConstants;
import com.lpxz.cmsauth.model.User;
import com.lpxz.cmsauth.model.dto.PhoneLoginDTO;
import com.lpxz.cmsauth.service.AuthService;
import com.lpxz.cmsauth.service.UserService;
import com.lpxz.cmscommon.base.BaseController;
import com.lpxz.cmscommon.util.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}")
    @ApiOperation("通过ID查询单个用户")
    public User findById(@ApiParam("ID") @PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping
    @ApiOperation("分页查询用户")
    public PageInfo<User> findByPage(@ApiParam("页号") @RequestParam(defaultValue = "1") Integer pageNum,
                                     @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.findByPage(pageNum, pageSize);
    }

    @PostMapping
    @ApiOperation("新增用户")
    public Resp insert(@RequestBody User user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkAccountUnique(user.getUserAccount()))) {
            return Resp.error("新增用户'" + user.getUserAccount() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return Resp.error("新增用户'" + user.getUserAccount() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return Resp.error("新增用户'" + user.getUserAccount() + "'失败，邮箱账号已存在");
        }
        return toResp(userService.insert(user), "insert user error.");
    }

    @PutMapping
    @ApiOperation("修改用户")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("通过ID删除单个用户")
    public void deleteById(@ApiParam("ID") @PathVariable("id") Long id) {
        userService.deleteById(id);
    }

    /**
     * 用电话号码登录
     *
     * @param phoneLoginDTO phoneLoginDTO
     * @return enterprise
     */
    @PostMapping("/login")
    public Resp login(@RequestBody @Validated PhoneLoginDTO phoneLoginDTO) {
        User user = userService.login(phoneLoginDTO);
        if (user == null) {
            return Resp.error("验证码错误或用户名不存在");
        }
        return Resp.success(user, "登录成功");
    }

    /**
     * 发送验证码
     *
     * @param json json
     * @return
     */
    @PostMapping("/captcha")
    public Resp send(@RequestBody JSONObject json) {
        var phone = json.getStr("phone");
        authService.sendCode(phone);
        return Resp.success("验证码已发送");
    }
}