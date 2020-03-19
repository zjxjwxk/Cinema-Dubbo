package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.UserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.UserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserAPI.class)
public class UserServiceImpl implements UserAPI {

    @Autowired
    private UserTMapper userTMapper;

    @Override
    public boolean register(UserModel userModel) {

        // 将注册信息实体转换为数据实体
        UserT userT = new UserT();
        userT.setUserName(userModel.getUsername());
        userT.setEmail(userModel.getEmail());
        userT.setAddress(userModel.getAddress());
        userT.setUserPhone(userModel.getPhone());
        // 默认创建时间和修改时间：current_timestamp

        // 数据加密【MD5混淆加密 + 盐值（Shiro项目有）】
        String md5Password = MD5Util.encrypt(userModel.getPassword());
        userT.setUserPwd(md5Password);

        // 将数据实体存入数据库
        Integer insert = userTMapper.insert(userT);
        return insert > 0;
    }

    @Override
    public int login(String username, String password) {

        // 根据登录账号获取数据库信息
        UserT userT = new UserT();
        userT.setUserName(username);
        UserT result = userTMapper.selectOne(userT);

        // 获取到的结果与加密后密码做匹配
        if (result != null && result.getUuid() > 0) {
            String md5Password = MD5Util.encrypt(password);
            if (result.getUserPwd().equals(md5Password)) {
                return result.getUuid();
            }
        }
        return 0;
    }

    @Override
    public boolean checkUserName(String username) {
        EntityWrapper<UserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name", username);
        Integer result = userTMapper.selectCount(entityWrapper);
        return result == null || result <= 0;
    }

    @Override
    public UserInfoModel getUserInfo(int uuid) {
        // 根据主键查询用户信息 [UserT]
        UserT userT = userTMapper.selectById(uuid);
        // 将 UserT 转换为 UserInfoModel 并返回
        return do2UserInfo(userT);
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        // 将传入的 UserInfoModel 转换为 UserT
        UserT userT = new UserT();
        userT.setUuid(userInfoModel.getUuid());
        userT.setNickName(userInfoModel.getNickname());
        userT.setUserSex(userInfoModel.getSex());
        userT.setBirthday(userInfoModel.getBirthday());
        userT.setEmail(userInfoModel.getEmail());
        userT.setUserPhone(userInfoModel.getPhone());
        userT.setAddress(userInfoModel.getAddress());
        userT.setHeadUrl(userInfoModel.getHeadAddress());
        userT.setBiography(userInfoModel.getBiography());
        userT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));

        // 将数据存入数据库
        Integer isSuccess = userTMapper.updateById(userT);
        if (isSuccess > 0) {
            // 根据 uuid 将用户信息查出来，并返回给前端
            return getUserInfo(userT.getUuid());
        } else {
            return userInfoModel;
        }
    }

    private UserInfoModel do2UserInfo(UserT userT) {

        UserInfoModel userInfoModel = new UserInfoModel();

        userInfoModel.setUuid(userT.getUuid());
        userInfoModel.setUsername(userT.getUserName());
        userInfoModel.setNickname(userT.getNickName());
        userInfoModel.setEmail(userT.getEmail());
        userInfoModel.setPhone(userT.getUserPhone());
        userInfoModel.setSex(userT.getUserSex());
        userInfoModel.setBirthday(userT.getBirthday());
        userInfoModel.setLifeState("" + userT.getLifeState());
        userInfoModel.setBiography(userT.getBiography());
        userInfoModel.setAddress(userT.getAddress());
        userInfoModel.setHeadAddress(userT.getHeadUrl());
        userInfoModel.setBeginTime(userT.getBeginTime().getTime());
        userInfoModel.setUpdateTime(userT.getUpdateTime().getTime());

        return userInfoModel;
    }
}
