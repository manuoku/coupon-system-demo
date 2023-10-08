package com.mnok.consumer.consumer;

import com.mnok.consumer.domain.Coupon;
import com.mnok.consumer.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;

    @KafkaListener(topics ="coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        // consumer 에서 쿠폰 생성
        couponRepository.save(new Coupon(userId));
    }
}
