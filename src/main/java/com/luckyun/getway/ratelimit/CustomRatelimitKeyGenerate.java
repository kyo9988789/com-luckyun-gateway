package com.luckyun.getway.ratelimit;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.Route;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties.Policy;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.DefaultRateLimitKeyGenerator;

/**
 * 自定义生成限流key
 * @author yangj080
 *
 */
public class CustomRatelimitKeyGenerate extends DefaultRateLimitKeyGenerator{

	public CustomRatelimitKeyGenerate(RateLimitProperties properties, RateLimitUtils rateLimitUtils) {
		super(properties, rateLimitUtils);
	}

	@Override
	public String key(HttpServletRequest request, Route route, Policy policy) {
		String requestUrl = request.getRequestURI();
		int strSubOperate = this.charIndex(requestUrl, lastIndex(requestUrl,'/'), '/');
		String operateResult = requestUrl.substring(strSubOperate + 1);
		//不进行限流
		if(operateResult.startsWith("read") || operateResult.startsWith("noAuth")
				|| operateResult.startsWith("noGetway")) {
			return UUID.randomUUID().toString();
		}else {
			//根据生成的cookie来确定请求来自于同一个浏览器
			String cookieName ="uniqueBrowserKey";
			String preKey = super.key(request, route, policy);
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length >= 1) {
				for(Cookie cookie : cookies) {
					if(cookieName.equals(cookie.getName())) {
						return cookie.getValue() + preKey;
					}
				}
			}
			return preKey;
		}
		//默认生成的key
		//return super.key(request, route, policy);
	}
	
	private Integer charIndex(String str, Integer record, char cr) {
		int i = 0;
		for (int j = 0; j < str.length(); j++) {
			char c = str.charAt(j);
			if (c == cr) {
				i++;
			}
			if (i == record) {
				return j;
			}
		}
		return 0;
	}
	private Integer lastIndex(String str,char cr) {
		int i = 0;boolean flag = false;
		for(int j = 0;j<str.length();j++) {
			char c = str.charAt(j);
			if(c == cr) {
				i++;
				flag = true;
				continue;
			}
			flag = false;
		}
		if(flag) {
			return i - 1;
		}
		return i;
	}
	
}
