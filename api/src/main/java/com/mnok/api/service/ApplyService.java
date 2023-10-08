package com.mnok.api.service;

import com.mnok.api.producer.CouponCreateProducer;
import com.mnok.api.repository.AppliedUserRepository;
import com.mnok.api.repository.CouponCountRepository;
import com.mnok.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    // 쿠폰발급 로직
    public void apply(Long userId) {
        // java 제공 synchronized 는 서버가 여러 대가 됐을 때 race condition 다시 발생해 적절하지 않음.
        // mysql, redis 를 활용한 lock 구현해 해결가능 --> 성능 불이익 발생 가능성.
        // 우리가 원하는건 쿠폰 개수에 대한 정합성.

        // redis 엔 incr 명령어가 존재. key 에 대한 value 를 +1씩 증가시키고 그 값을 리턴하는 명령어.
        // redis 는 single thread 기반으로 동작해, race condition 해결 뿐만 아니라 incr 명령어는 성능도 굉장히 빠른 명령어.
        // incr 명령어를 사용해 발급된 쿠폰 개수 제어시 성능도 빠르며, 데이터 정합성도 지킬 수 있음.

        //long count = couponRepository.count();
        // 쿠폰 발급 전 쿠폰 개수를 증가시키고 발급된 쿠폰의 개수가 100개 보다 많다면 발급하지 않도록 변경하였음.

        Long apply = appliedUserRepository.add(userId);

        if (apply != 1) return;


        Long count = couponCountRepository.increment();

        if(count > 100) return;

        //couponRepository.save(new Coupon(userId));
        couponCreateProducer.create(userId);
    }
}
