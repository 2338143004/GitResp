package com.mashibing.serviceverificationcode.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.responese.NumberCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public ResponseResult numberCode(@PathVariable("size") int size){
        System.out.println("szie:"+size);
        //生成验证码
        double mathRandom = (Math.random() * 9 + 1) * (Math.pow(10,size-1));
        System.out.println(mathRandom);
        int resultInt = (int)mathRandom;
        System.out.println(resultInt);

        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",1);
        jsonObject.put("message","success");
        JSONObject data = new JSONObject();
        data.put("numberCode",resultInt);
        jsonObject.put("data",data);*/
        //将验证码存入对象
        NumberCodeResponse numberCodeResponse = new NumberCodeResponse();
        numberCodeResponse.setNumberCode(resultInt);
        //返回值
        return ResponseResult.success(numberCodeResponse);
    }
}
