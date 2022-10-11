package com.mashibing.apipassenger.service;

import com.mashibing.apipassenger.remote.ServicePassengerUserClient;
import com.mashibing.internalcommon.dto.PassengerUser;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.dto.TokenResult;
import com.mashibing.internalcommon.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult getUserByAccessToken(String accessToken){
        log.info("accessToken:"+accessToken);

        //解析accessToken
        TokenResult accessTokenResult = JwtUtils.checkToken(accessToken);
        //从对象中获取手机号
        String phone = accessTokenResult.getPhone();
        log.info("手机号："+phone);

        //根据手机号查询用户信息
        ResponseResult userByPhone = servicePassengerUserClient.getUserByPhone(phone);

        ResponseResult success = ResponseResult.success(userByPhone.getData());
        return success;
    }


}
