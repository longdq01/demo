package com.example.consumer.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class PaymentPartnerDTO extends CreatePaymentReqDTO{
    @JsonProperty("queueNameResponse")
    private String queueNameResponse;
}
