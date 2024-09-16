package com.example.producer.service;

import com.example.producer.config.RabbitmqConfig;
import com.example.producer.model.BaseResponse;
import com.example.producer.repository.rabbitmq.RabbitmqService;
import com.example.producer.repository.redis.RedisService;
import com.example.producer.exception.ApiException;
import com.example.producer.model.dto.CreatePaymentReqDTO;
import com.example.producer.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqConfig rabbitmqConfig;

    @Override
    public BaseResponse createPayment(CreatePaymentReqDTO createPaymentReqDTO) {
        log.info("[PaymentServiceImpl - createPayment] create payment with reqDto: {}", createPaymentReqDTO);
        validatePaymentData(createPaymentReqDTO);
        String response = rabbitmqService.sendRPCMessage(rabbitmqConfig.getPaymentExchange(),
                rabbitmqConfig.getPaymentRoutingKey(),
                createPaymentReqDTO);
        log.info("[PaymentServiceImpl - createPayment] response from queue: {}", response);
        BaseResponse baseResponse = JsonUtils.parseJson(response, BaseResponse.class);
        if(baseResponse.getCode() == HttpsURLConnection.HTTP_INTERNAL_ERROR)
            throw ApiException.ErrInternal().build();
        cacheTokenKey(createPaymentReqDTO.getTokenKey());
        return baseResponse;
    }

    private void validatePaymentData(CreatePaymentReqDTO createPaymentReqDTO) {
        int realAmount;
        try{
            realAmount = Integer.parseInt(createPaymentReqDTO.getRealAmount());
        }catch (NumberFormatException ex){
            throw ApiException.ErrInvalidData().message("realAmount is invalid").build();
        }
        int dif = createPaymentReqDTO.getDebitAmount() - realAmount;

        if(dif < 0){
            throw ApiException.ErrInvalidData().message("realAmount must be greater than debitAmount").build();
        }
        if(dif > 0 && (createPaymentReqDTO.getPromotionCode() == null || createPaymentReqDTO.getPromotionCode().isBlank())){
            throw ApiException.ErrInvalidData().message("promotionCode is invalid").build();
        }
        if(redisService.existKey(createPaymentReqDTO.getTokenKey())){
            throw ApiException.ErrInvalidData().message("token already exists").build();
        }
    }

    private void cacheTokenKey(String tokenKey){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date expireAtDate = Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        redisService.setKeyExpireAt(tokenKey, tokenKey, expireAtDate);
    }
}
