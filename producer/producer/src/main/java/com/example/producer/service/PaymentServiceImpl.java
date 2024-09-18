package com.example.producer.service;

import com.example.producer.cache.CacheCreatePaymentResponse;
import com.example.producer.config.Config;
import com.example.producer.config.RabbitmqConfig;
import com.example.producer.model.BaseResponse;
import com.example.producer.model.dto.CreatePaymentResDTO;
import com.example.producer.repository.rabbitmq.RabbitmqService;
import com.example.producer.repository.redis.RedisService;
import com.example.producer.exception.ApiException;
import com.example.producer.model.dto.CreatePaymentReqDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqConfig rabbitmqConfig;

    @Autowired
    private CacheCreatePaymentResponse cacheCreatePaymentResponse;

    @Autowired
    private Config config;

    @Override
    public BaseResponse createPayment(CreatePaymentReqDTO createPaymentReqDTO) {
        createPaymentReqDTO.setApiID(config.getServiceId());
        log.info("Token [{}] - Begin create payment with reqDto: {}", createPaymentReqDTO.getTokenKey(), createPaymentReqDTO);
        validatePaymentData(createPaymentReqDTO);
        cacheTokenKey(createPaymentReqDTO.getTokenKey());
        rabbitmqService.sendMessage(rabbitmqConfig.getPaymentExchange(),
                rabbitmqConfig.getPaymentRoutingKey(),
                createPaymentReqDTO,
                createPaymentReqDTO.getTokenKey());
        CreatePaymentResDTO responseSystem = waitCreatePaymentResponse(createPaymentReqDTO.getTokenKey());
        if(responseSystem == null){
            log.info("Token [{}] - receive create payment response time out, data: {}", createPaymentReqDTO.getTokenKey(), createPaymentReqDTO);
            throw ApiException.ErrTimeout().build();
        }
        log.info("Token [{}] - receive create payment response: {}", createPaymentReqDTO.getTokenKey(), responseSystem);
        return BaseResponse.builder()
                .code(responseSystem.getCode())
                .message(responseSystem.getMessage())
                .build();
    }

    private void validatePaymentData(CreatePaymentReqDTO createPaymentReqDTO) {
        log.info("Token [{}] - Begin validate payment data: {}", createPaymentReqDTO.getTokenKey(), createPaymentReqDTO);
        int realAmount;
        try{
            realAmount = Integer.parseInt(createPaymentReqDTO.getRealAmount());
        }catch (NumberFormatException e){
            log.error("Token [{}] - Validate failed: realAmount must be number, error: {}", createPaymentReqDTO.getTokenKey(), e);
            throw ApiException.ErrInvalidData().message("realAmount is invalid").build();
        }
        int dif = createPaymentReqDTO.getDebitAmount() - realAmount;

        if(dif < 0){
            log.info("Token [{}] - realAmount must be greater than debitAmount", createPaymentReqDTO.getTokenKey());
            throw ApiException.ErrInvalidData().message("realAmount must be greater than debitAmount").build();
        }
        if(dif > 0 && (createPaymentReqDTO.getPromotionCode() == null || createPaymentReqDTO.getPromotionCode().isBlank())){
            log.info("Token [{}] - promotionCode is invalid", createPaymentReqDTO.getTokenKey());
            throw ApiException.ErrInvalidData().message("promotionCode is invalid").build();
        }
        if(redisService.existKey(createPaymentReqDTO.getTokenKey())){
            log.info("Token [{}] - token already exists", createPaymentReqDTO.getTokenKey());
            throw ApiException.ErrInvalidData().message("token already exists").build();
        }
        log.info("Token [{}] - Validate create payment data success: {}", createPaymentReqDTO.getTokenKey(), createPaymentReqDTO);
    }

    private void cacheTokenKey(String tokenKey){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date expireAtDate = Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        log.info("Token [{}] - Cache token key expire at: {}", tokenKey, expireAtDate);
        redisService.setKeyExpireAt(tokenKey, tokenKey, expireAtDate);
    }

    private CreatePaymentResDTO waitCreatePaymentResponse(String tokenKey){
        int count = 0;
        while (true) {
            if (cacheCreatePaymentResponse.containsKey(tokenKey)) {
                CreatePaymentResDTO responseSystem = cacheCreatePaymentResponse.get(tokenKey);
                cacheCreatePaymentResponse.remove(tokenKey);
                log.info("Token [{}] - End receive response data: {}.", tokenKey, responseSystem.toString());
                return responseSystem;
            }
//            synchronized (this) {
//                try {
//                    this.wait(1000);
//                    count++;
//                } catch (InterruptedException ex) {
//                    log.error("Token [{}] - Wait from server failed by ex: {}", tokenKey, ex);
//                }
//            }
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException ex) {
                log.error("Token [{}] - Wait from server failed by ex: {}", tokenKey, ex);
            }
            if (count > 100) {
                log.error("Token [{}] - End Time out from server", tokenKey);
                return null;
            }
        }
    }
}
