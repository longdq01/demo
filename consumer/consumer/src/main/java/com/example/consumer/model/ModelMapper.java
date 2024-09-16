package com.example.consumer.model;

import com.example.consumer.model.dto.CreatePaymentReqDTO;
import com.example.consumer.model.dto.PaymentPartnerDTO;
import com.example.consumer.model.entity.PaymentEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Slf4j
public class ModelMapper {
    public static PaymentEntity paymentDTOToPaymentEntity(CreatePaymentReqDTO paymentDTO) {
        return PaymentEntity.builder()
                .orderCode(paymentDTO.getOrderCode())
                .tokenKey(paymentDTO.getTokenKey())
                .apiID(paymentDTO.getApiID())
                .mobile(paymentDTO.getMobile())
                .bankCode(paymentDTO.getBankCode())
                .accountNo(paymentDTO.getAccountNo())
                .payDate(paymentDTO.getPayDate())
                .additionalData(paymentDTO.getAdditionalData())
                .debitAmount(paymentDTO.getDebitAmount())
                .respCode(paymentDTO.getRespCode())
                .respDesc(paymentDTO.getRespDesc())
                .traceTransfer(paymentDTO.getTraceTransfer())
                .messageType(paymentDTO.getMessageType())
                .checkSum(paymentDTO.getCheckSum())
                .username(paymentDTO.getUserName())
                .realAmount(Integer.parseInt(paymentDTO.getRealAmount()))
                .promotionCode(paymentDTO.getPromotionCode())
                .addValue(paymentDTO.getAddValue())
                .build();
    }

    public static PaymentPartnerDTO paymentDTOToPaymentPartnerDTO(CreatePaymentReqDTO paymentDTO) {
        PaymentPartnerDTO paymentPartnerDTO = new PaymentPartnerDTO();
        copyProperties(paymentPartnerDTO, paymentDTO);
        paymentPartnerDTO.setQueueNameResponse("queue.payment.qrcodeV2.restPayment");
        return paymentPartnerDTO;
    }

    private static void copyProperties(Object dest, Object orig){
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            log.info("[ModelMapper - copyProperties] error while mapping object: {}", e.getMessage());
        }
    }
}
