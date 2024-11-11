package net.allbareum.allbareumbackend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 기본적으로 유지할 스레드 수
        executor.setMaxPoolSize(10);  // 최대 스레드 수
        executor.setQueueCapacity(100);  // 큐에 쌓이는 작업의 수
        executor.setThreadNamePrefix("AsyncThread-");  // 스레드 이름 접두사
        executor.initialize();
        return executor;
    }
}
