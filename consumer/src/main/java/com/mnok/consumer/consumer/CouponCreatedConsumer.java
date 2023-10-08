package com.mnok.consumer.consumer;

import com.mnok.consumer.domain.Coupon;
import com.mnok.consumer.domain.FailedEvent;
import com.mnok.consumer.repository.CouponRepository;
import com.mnok.consumer.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;


    @KafkaListener(topics ="coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            log.error("failed to create coupon :: " + userId);
            failedEventRepository.save(new FailedEvent(userId));
        }

    }
}
