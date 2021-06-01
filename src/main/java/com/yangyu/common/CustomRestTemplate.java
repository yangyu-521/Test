package com.yangyu.common;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CustomRestTemplate {
    @Value("${com.yangyu.fetch.weather.retry_times}")
    private int retryTimes;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        int iTmp = 0;
        for (HttpMessageConverter<?> messageConverter: messageConverters) {
            if (messageConverter.getClass().equals(StringHttpMessageConverter.class)) {
                messageConverters.set(iTmp, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            }
            iTmp++;
        }
    }

    @Async()
    public JSONObject getInfo(String url) {
        int counter = 0;
        while (counter < retryTimes) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                return (JSONObject) JSONObject.parse(response.getBody());
            } catch (Exception e) {
                if (e.getMessage().contains("connection")) {
                    try {
                        Thread.sleep(5 * 1000);
                        counter++;
                        log.info("Maybe there is a connection exception and will try again with counter:{}", counter);
                    } catch (InterruptedException interruptedException) {
                        log.error("There is an exception when waiting for try again:{}", interruptedException.getMessage());
                    }
                }
            }
        }
        return null;
    }

}
