package com.example.producer.cache;

import com.example.producer.model.dto.CreatePaymentResDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Component
public class CacheCreatePaymentResponse {
    private final ConcurrentMap<String, CreatePaymentResDTO> cacheResponse;

    public CacheCreatePaymentResponse() {
        this.cacheResponse = new ConcurrentHashMap<>(10000);
    }

    public CreatePaymentResDTO get(String tokenKey) {
        return cacheResponse.get(tokenKey);
    }

    public synchronized void put(String tokenKey, CreatePaymentResDTO createPaymentResDTO) {
        if(!cacheResponse.containsKey(tokenKey)) {
            cacheResponse.put(tokenKey, createPaymentResDTO);
        }
    }

    public synchronized void remove(String tokenKey) {
        cacheResponse.remove(tokenKey);
    }

    public boolean containsKey(String tokenKey) {
        return cacheResponse.containsKey(tokenKey);
    }
}
