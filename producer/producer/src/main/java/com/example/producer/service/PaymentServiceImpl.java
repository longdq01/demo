package com.example.producer.service;

import com.example.producer.config.RabbitmqConfig;
import com.example.producer.repository.RabbitmqService;
import com.example.producer.repository.RedisService;
import com.example.producer.exception.ApiException;
import com.example.producer.model.CreatePaymentReqDTO;
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
    public void createPayment(CreatePaymentReqDTO createPaymentReqDTO) {
        log.info("[PaymentServiceImpl - createPayment] create payment with reqDto: {}", createPaymentReqDTO);
        validatePaymentData(createPaymentReqDTO);
        String response = rabbitmqService.sendCorrelationMessage(rabbitmqConfig.getPaymentExchange(),
                rabbitmqConfig.getPaymentRoutingKey(),
                createPaymentReqDTO);
        cacheTokenKey(createPaymentReqDTO.getTokenKey());
    }

    private void validatePaymentData(CreatePaymentReqDTO createPaymentReqDTO) {
        int realAmount;
        try{
            realAmount = Integer.parseInt(createPaymentReqDTO.getRealAmount());
        }catch (NumberFormatException ex){
            throw new ApiException(HttpsURLConnection.HTTP_BAD_REQUEST, "realAmount is invalid");
        }

        int dif = createPaymentReqDTO.getDebitAmount() - realAmount;
        if(dif < 0){
            throw new ApiException(HttpsURLConnection.HTTP_BAD_REQUEST, "realAmount must be greater than debitAmount");
        }
        if(dif > 0 && (createPaymentReqDTO.getPromotionCode() == null || createPaymentReqDTO.getPromotionCode().isBlank())){
            throw new ApiException(HttpsURLConnection.HTTP_BAD_REQUEST, "promotionCode is invalid");
        }
        if(redisService.existKey(createPaymentReqDTO.getTokenKey())){
            throw new ApiException(HttpsURLConnection.HTTP_BAD_REQUEST, "token already exists");
        }
    }

    private void cacheTokenKey(String tokenKey){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date expireAtDate = Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        redisService.setKeyExpireAt(tokenKey, tokenKey, expireAtDate);
    }
}
