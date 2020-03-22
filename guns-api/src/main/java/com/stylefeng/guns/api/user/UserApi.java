package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;

/**
 * @author zjxjwxk
 */
public interface UserApi {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return userId
     */
    int login(String username, String password);

    /**
     * 用户注册
     * @param userModel 注册表单
     * @return 是否注册成功
     */
    boolean register(UserModel userModel);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否已存在
     */
    boolean checkUserName(String username);

    /**
     * 获取用户信息
     * @param uuid uuid
     * @return 用户信息
     */
    UserInfoModel getUserInfo(int uuid);

    /**
     * 更新用户信息
     * @param userInfoModel 要更新的用户信息
     * @return 更新后的用户信息
     */
    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}
