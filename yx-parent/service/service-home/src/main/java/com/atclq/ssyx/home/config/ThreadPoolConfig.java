package com.atclq.ssyx.home.config;
//自定义线程池
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                          //corePoolSize：线程池中的常驻核心线程数
                5,                                      //maximumPoolSize：线程池中能够容纳同时 执行的最大线程数，此值必须大于等于1
                2,                                      //keepAliveTime：多余的空闲线程的存活时间 当前池中线程数量超过corePoolSize时，当空闲时间达到keepAliveTime时，多余线程会被销毁直到 只剩下corePoolSize个线程为止
                TimeUnit.SECONDS,                       //unit：keepAliveTime的单位
                new ArrayBlockingQueue<>(3),    //workQueue：任务队列，被提交但尚未被执行的任务
                Executors.defaultThreadFactory(),       //threadFactory：表示生成线程池中工作线程的线程工厂， 用于创建线程，**一般默认的即可**
                new ThreadPoolExecutor.AbortPolicy()    //handler：拒绝策略，表示当队列满了，并且工作线程大于 等于线程池的最大线程数（maximumPoolSize）时，如何来拒绝 请求执行的runnable的策略
                // ThreadPoolExecutor自带的拒绝策略如下：
                //   1. AbortPolicy(默认)：直接抛出RejectedExecutionException异常阻止系统正常运行
                //   2. CallerRunsPolicy：“调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量。
                //   3. DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加人队列中 尝试再次提交当前任务。
                //   4. DiscardPolicy：该策略默默地丢弃无法处理的任务，不予任何处理也不抛出异常。 如果允许任务丢失，这是最好的一种策略。
        );
        return executor;
    }
}
