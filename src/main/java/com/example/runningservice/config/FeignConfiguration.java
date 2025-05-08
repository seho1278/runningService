package com.example.runningservice.config;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfiguration {

    @Value("${mailgun.api-key}")
    private String apiKey;

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("api", apiKey);
    }

    @Bean
    public Encoder feignFormEncoder() {
        // application/x-www-form-urlencoded 형식으로 데이터를 인코딩하기 위해 SpringFormEncoder 등록
        return new SpringFormEncoder(new SpringEncoder(this.messageConverters));
    }
}
