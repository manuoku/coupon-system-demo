package com.mnok.api.service;

import com.mnok.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("쿠폰1개_응모")
    @Test
    public void 한번만응모() {
        applyService.apply(1L);

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("동시에_여러개_응모")
    @Test
    public void 여러명응모() throws InterruptedException {
        //동시에 여러개 요청을 보내야 하기에 멀티스레드 사용해야 함
        int threadCount = 1000;

        //ExecutorService 는 병렬 작업을 간단히 할 수 있게 도와주는 java api
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //모든 요청 끝날 때까지 기다려야함
        //다른 스레드에서 수행하는 작업을 기다리도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < 1000; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // kafka : producer 전송 시점과 consumer 처리 시점 불일치 해소를 위한 thread sleep
        Thread.sleep(10000);

        long count = couponRepository.count();
        assertThat(count).isEqualTo(100);

        /*
        * Race condition 발생 :
        * race condition 란 두 개 이상의 스레드가 공유 데이터에 access를 하고,
        * 동시에 작업을 하려고 할 때 발생하는 문제
        *
        * -> single thread로 작업시 race condition 미발생
        * --> redis 를 활용해 race condition 해결
        * */

    }

    /*
    * redis 는 single thread 기반으로 동작함.
    *
    * */
}