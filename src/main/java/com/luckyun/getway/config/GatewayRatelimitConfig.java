package com.luckyun.getway.config;

import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties.PREFIX;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.luckyun.getway.ratelimit.CustomRatelimitKeyGenerate;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitKeyGenerator;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
public class GatewayRatelimitConfig {
	
	@Bean
	public RateLimitKeyGenerator customRateLimitKeyGenerater(RateLimitProperties properties,
			RateLimitUtils rateLimitUtils) {
		return new CustomRatelimitKeyGenerate(properties,rateLimitUtils);
	}
}
