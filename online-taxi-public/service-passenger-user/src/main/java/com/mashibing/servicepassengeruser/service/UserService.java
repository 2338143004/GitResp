package com.mashibing.servicepassengeruser.service;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.dto.VerificationCodeDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {
    public ResponseResult loginOrRegister(String passengerPhone){
        System.out.println("User service");
        //根据手机号查询用户信息

        //判断用户信息是否存在

        //如果不存在，插入用户信息
        return ResponseResult.success();
    }

}
