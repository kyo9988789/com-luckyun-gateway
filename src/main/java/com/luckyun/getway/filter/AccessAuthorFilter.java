package com.luckyun.getway.filter;

import javax.servlet.http.Cookie;

import org.springframework.util.StringUtils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
/**
 * 网管过滤器（第二层），判断是否授权
*
 * 2019年05月22日,上午10:30 
 * {@link com.lucksoft.getway.filter.AccessAuthenFilter}
 * 
 * @author yangj080
 * @version 1.0.0
 *
 */
public class AccessAuthorFilter extends ZuulFilter{
	
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
		return 1;
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

	private static final String NOFILTER = "notFilter";
	
	/**
	 * 判断是否授权
	 */
	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
        if(!ctx.getBoolean(NOFILTER)) {
            String auth = ctx.getRequest().getHeader("Authorization");
            if(StringUtils.isEmpty(auth)) {
            	auth = ctx.getRequest().getParameter("token");
            	if(StringUtils.isEmpty(auth)) {
            		Cookie[] cookies = ctx.getRequest().getCookies();
            		if(cookies != null && cookies.length >= 1) {
						for(Cookie cookie : cookies) {
							if("LUCK_AUTH_MANAGER".equals(cookie.getName())) {
								auth = cookie.getValue();
								break;
							}
						}
					}
            	}
            }
        	// 如果请求的Authorization参数token不为空，说明已经授权登录则通过
	        if(null != auth) {
	        	if(auth.startsWith("Bearer ")) {
	        		ctx.set("token",auth);
	        	}else {
	        		ctx.set("token","Bearer " + auth);
	        	}
	        	// 过滤该请求，对其进行路由
	        	ctx.setSendZuulResponse(Boolean.TRUE);
	            ctx.setResponseStatusCode(200);
	            ctx.set("isSuccess", Boolean.TRUE);
	        }else {
	        	// 过滤该请求，不对其进行路由
	        	ctx.setSendZuulResponse(Boolean.FALSE);
	        	// 返回错误码
	            ctx.setResponseStatusCode(401);
	            // 返回错误内容
	            ctx.setResponseBody("{\"code\":0,\"result\":\"token missing!\"}");
	            ctx.set("isSuccess", Boolean.FALSE);
	        }
        }else {
        	// 过滤该请求，对其进行路由
        	ctx.setSendZuulResponse(Boolean.TRUE);
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess", Boolean.TRUE);
        }
        return null;
	}
	
}
