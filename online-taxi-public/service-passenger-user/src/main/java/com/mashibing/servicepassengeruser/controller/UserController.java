package com.mashibing.servicepassengeruser.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicepassengeruser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    //@RequestMapping(method = RequestMethod.POST,value = "/user")
    @PostMapping("/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationCodeDTO verificationCodeDTO){
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("手机号："+passengerPhone);
        ResponseResult responseResult = userService.loginOrRegister(passengerPhone);
        return responseResult;
    }

    @GetMapping("/user/{phone}")
    public ResponseResult getUserByPhone(@PathVariable("phone") String passengerPhone){
        System.out.println("passengerPhone:"+passengerPhone);
        return userService.getUser(passengerPhone);
    }
}
