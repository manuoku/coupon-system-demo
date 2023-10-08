package com.mnok.api.service;

import com.mnok.api.producer.CouponCreateProducer;
import com.mnok.api.repository.AppliedUserRepository;
import com.mnok.api.repository.CouponCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ApplyService {

    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    // 쿠폰발급 로직
    public void apply(Long userId) {

        Long apply = appliedUserRepository.add(userId);

        if(apply != 1) return;

        Long count = couponCountRepository.increment();

        if(count > 100) return;

        couponCreateProducer.create(userId);
    }
}
