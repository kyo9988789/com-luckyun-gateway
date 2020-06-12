package com.luckyun.getway.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.luckyun.getway.config.GetwayBaseConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 网管过滤器（第一层），判断URL是否需要拦截
 * 
 * 2019年05月22日,上午10:30 {@link com.lucksoft.getway.filter.AccessOnlyRouter}
 * 
 * @author yangj080
 * @version 1.0.0
 *
 */
@Component
public class AccessOnlyRouter extends ZuulFilter {

	@Autowired
	private GetwayBaseConfig getwayBaseConfig;

	/**
	 * 是否需要执行该filter，true表示执行，false表示不执行
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * 定义filter的顺序，数字越小表示顺序越高，越先执行
	 */
	@Override
	public int filterOrder() {
		return 0;
	}

	/**
	 * 定义filter的类型，有pre、route、post、error四种
	 * pre：可以在请求被路由之前调用 
	 * route：在路由请求时候被调用 
	 * post：在route和error过滤器之后被调用
	 * error：处理请求时发生错误时被调用
	 */
	@Override
	public String filterType() {
		// 前置过滤器
		return "pre";
	}

	/**
	 * 设置那些URL需要拦截，那些不需要拦截
	 */
	@Override
	public Object run() throws ZuulException {
		/**
		 * 一些url不进行拦截
		 */
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		String requestUrl = request.getRequestURI();
		try {
			// 字符串中符合条件的数据
			int strSub = this.charIndex(requestUrl, 2, '/');
			String realUrl = requestUrl.substring(strSub + 1);
			int strSubOperate = this.charIndex(requestUrl, lastIndex(requestUrl,'/'), '/');
			String operateResult = requestUrl.substring(strSubOperate + 1);
			List<String> strings = this.getwayBaseConfig.getNoInterceptorUrl();
			if (strings.contains(realUrl)) {
				ctx.set("notFilter", true);
			} else {
				ctx.set("notFilter", false);
			}
			List<String> container = getwayBaseConfig.getContainer();
			List<String> endWith = getwayBaseConfig.getEndwith();
			List<String> startWith = getwayBaseConfig.getStartwith();
			for(String ew : endWith) {
				if(!StringUtils.isEmpty(ew) && operateResult.endsWith(ew)) {
					ctx.set("notFilter", true);
					break;
				}
			}
			for(String sw : startWith) {
				if(!StringUtils.isEmpty(sw) && operateResult.startsWith(sw)) {
					ctx.set("notFilter", true);
					break;
				}
			}
			for(String ct : container) {
				if(!StringUtils.isEmpty(ct) && requestUrl.contains(ct)) {
					ctx.set("notFilter", true);
					break;
				}
			}
			String headerInterceptor = request.getHeader(getwayBaseConfig.getHeaderNotInterceptor());
			if(!StringUtils.isEmpty(headerInterceptor) && Boolean.parseBoolean(headerInterceptor)) {
				ctx.set("notFilter", true);
			}
			//后续记录访问日志
			
		} catch (Exception e) {
			return null;
		}
		return null;
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
