package com.mnok.api.service;

import com.mnok.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        int threadCount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

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

        // producer 전송 시점과 consumer 처리 시점 불일치 해소
        Thread.sleep(10000);

        long count = couponRepository.count();
        assertThat(count).isEqualTo(100);
    }

    @DisplayName("한명당_한개의쿠폰만_발급")
    @Test
    public void 한명당_한개의쿠폰만_발급() throws InterruptedException {
        int threadCount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < 1000; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Thread.sleep(10000);

        long count = couponRepository.count();
        assertThat(count).isEqualTo(1);
    }
}