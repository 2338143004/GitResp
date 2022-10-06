package com.mashibing.apipassenger.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.mashibing.apipassenger.remote.ServiceVefificationcodeClient;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.responese.NumberCodeResponse;
import com.mashibing.internalcommon.responese.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVefificationcodeClient serviceVefificationcodeClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


    //乘客验证码前缀
    private String verificationCodePrefix = "passenger-verification-code-";
    /**
     * 生成验证码
     * @param passengerPhone 手机号
     * @return
     */
    public ResponseResult generatorCode(String passengerPhone){
        //调用验证码服务
        System.out.println("调用验证码服务");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVefificationcodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();
        System.out.println("remote numberf code:"+numberCode);

        //存入redis
        System.out.println("存入redis");
        String key = generatorKeyByPhone(passengerPhone);
        //String key = "helloworld";
        //key.value,过期时间
        redisTemplate.opsForValue().set(key,numberCode+"",2, TimeUnit.MINUTES);
        // 通过短信服务商，将对应的验证码发送到手机上。阿里短信服务，腾讯短信通，华信，容联
        System.out.println("通过短信服务商，将对应的验证码发送到手机上。阿里短信服务，腾讯短信通，华信，容联");

        //返回值
        return ResponseResult.success("");
    }

    /**
     * 根据手机号，生成key
     * @param passengerPhone
     * @return
     */
    public String generatorKeyByPhone(String passengerPhone){
        return verificationCodePrefix+passengerPhone;
    }

    /**
     * 校验验证码
     * @param passengerPhone
     * @param verificationCode
     * @return
     */
    public ResponseResult checkCode(String passengerPhone,String verificationCode){
        //根据手机号，去redis读取验证码
        System.out.println("根据手机号，去redis读取验证码");

        //获取key
        String key = generatorKeyByPhone(passengerPhone);
        //根据key获取value
        String codeRedis = redisTemplate.opsForValue().get(key);
        System.out.println("redis中的value:"+codeRedis);

        //校验验证码
        System.out.println("校验验证码");
        if(StringUtils.isBlank(codeRedis)){
            System.out.println("验证码为空！");
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if(!verificationCode.trim().equals(codeRedis.trim())){
            System.out.println("验证码错误！");
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        //判断原来是否有用户，并进行对应的处理
        System.out.println("判断原来是否有用户，并进行对应的处理");

        //颁发令牌
        System.out.println("颁发令牌");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken("token value");

        return ResponseResult.success(tokenResponse);
    }

}
