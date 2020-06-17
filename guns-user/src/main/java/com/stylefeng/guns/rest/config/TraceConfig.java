package com.stylefeng.guns.rest.config;

import brave.spring.beans.TracingFactoryBean;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author zjxjwxk
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zipkin")
public class TraceConfig {

    private String address;

    @Bean("tracing")
    public TracingFactoryBean getTracingBean() {
        TracingFactoryBean tracingFactoryBean = new TracingFactoryBean();
        tracingFactoryBean.setLocalServiceName("user");
        tracingFactoryBean.setSpanReporter(AsyncReporter.create(OkHttpSender.create("http://" + address + ":9411/api/v2/spans")));
        return tracingFactoryBean;
    }
}
