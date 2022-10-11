package com.mashibing.apipassenger.service;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.mashibing.internalcommon.constant.TokenConstants;
import com.mashibing.apipassenger.remote.ServicePassengerUserClient;
import com.mashibing.apipassenger.remote.ServiceVefificationcodeClient;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.IdentityConstants;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.VerificationCodeDTO;
import com.mashibing.internalcommon.responese.NumberCodeResponse;
import com.mashibing.internalcommon.responese.TokenResponse;
import com.mashibing.internalcommon.util.JwtUtils;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVefificationcodeClient serviceVefificationcodeClient;
    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    @Autowired
    private StringRedisTemplate redisTemplate;//redis对象

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
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
        //String key = "helloworld";
        //key.value,过期时间
        redisTemplate.opsForValue().set(key,numberCode+"",2, TimeUnit.MINUTES);
        // 通过短信服务商，将对应的验证码发送到手机上。阿里短信服务，腾讯短信通，华信，容联
        System.out.println("通过短信服务商，将对应的验证码发送到手机上。阿里短信服务，腾讯短信通，华信，容联");

        //返回值
        return ResponseResult.success("");
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
        System.out.println("接收到的验证码:"+verificationCode);
        //获取key
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
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
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        //颁发令牌
        System.out.println("颁发令牌");
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY,TokenConstants.REFRESH_TOKEN_TYPE);
        System.out.println("accessToken:"+accessToken);
        System.out.println("refreshToken:"+refreshToken);

        //将token存到redis中
        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone,IdentityConstants.PASSENGER_IDENTITY,TokenConstants.ACCESS_TOKEN_TYPE);
        System.out.println("accessTokenKey:"+accessTokenKey);
        redisTemplate.opsForValue().set(accessTokenKey,accessToken,30,TimeUnit.DAYS);

        //将另一个token存到redis中
        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        System.out.println("refreshTokenKey:"+refreshTokenKey);
        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,31,TimeUnit.DAYS);
        /*//测试
        redisTemplate.opsForValue().set(accessTokenKey,accessToken,15,TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,50,TimeUnit.SECONDS);*/

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseResult.success(tokenResponse);
    }

}
