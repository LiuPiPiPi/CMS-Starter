package com.lpxz.cmsauth.controller;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import com.lpxz.cmsauth.constant.UserConstants;
import com.lpxz.cmsauth.model.User;
import com.lpxz.cmsauth.model.dto.PhoneLoginDTO;
import com.lpxz.cmsauth.service.AuthService;
import com.lpxz.cmsauth.service.UserService;
import com.lpxz.cmscommon.base.BaseController;
import com.lpxz.cmscommon.util.response.Result;
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

    @ApiOperation("通过ID查询单个用户")
    @GetMapping("/{id}")
    public Result<User> findById(@ApiParam("ID") @PathVariable Long id) {
        return toResp(userService.findById(id), "no such user.");
    }

    @ApiOperation("通过电话查询单个用户")
    @GetMapping("/phone/{phone}")
    public Result<User> findByPhone(@ApiParam("电话") @PathVariable String phone) {
        return toResp(userService.findUserByPhone(phone), "no such user.");
    }

    @ApiOperation("通过邮箱查询单个用户")
    @GetMapping("/email/{email}")
    public Result<User> findByEmail(@ApiParam("邮箱") @PathVariable String email) {
        return toResp(userService.findUserByEmail(email), "no such user.");
    }

    @ApiOperation("分页查询用户")
    @GetMapping("/list")
    public Result<PageInfo<User>> findByPage(@ApiParam("页号") @RequestParam(defaultValue = "1") Integer pageNum,
                                             @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(userService.findByPage(pageNum, pageSize));
    }

    @ApiOperation("新增用户")
    @PostMapping("/insert")
    public Result<?> insert(@RequestBody User user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkAccountUnique(user.getUserAccount()))) {
            return error("新增用户'" + user.getUserAccount() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return error("新增用户'" + user.getUserAccount() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return error("新增用户'" + user.getUserAccount() + "'失败，邮箱账号已存在");
        }
        return toResp(userService.insert(user), "insert user error.");
    }


    @ApiOperation("修改用户")
    @PostMapping("/update")
    public Result<?> update(@RequestBody User user) {
        return toResp(userService.update(user));
    }


    @ApiOperation("通过ID删除单个用户")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteById(@ApiParam("ID") @PathVariable("id") Long id) {
        return toResp(userService.deleteById(id), "delete error!");
    }

    @ApiOperation("用电话号码登录")
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Validated PhoneLoginDTO phoneLoginDTO) {
        User user = userService.login(phoneLoginDTO);
        if (user == null) {
            return error("验证码错误或用户名不存在");
        }
        return success(user, "登录成功");
    }

    @ApiOperation("发送验证码")
    @PostMapping("/captcha")
    public Result<?> send(@RequestBody JSONObject json) {
        var phone = json.getStr("phone");
        authService.sendCode(phone);
        return success("验证码已发送");
    }
}
