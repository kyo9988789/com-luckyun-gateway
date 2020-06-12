package com.luckyun.getway.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luckyun.auth.provider.feign.AuthSysModuleProvider;
import com.luckyun.getway.config.GetwayBaseConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 
 * 网管过滤器（第三层）
 * 
 * 2019年05月22日,上午10:30 
 * {@link com.lucksoft.getway.filter.AccessAuthenFilter}
 * 
 * @author yangj080
 * @version 1.0.0
 *
 */
@Component
public class AccessAuthenFilter extends ZuulFilter {
	
	@Autowired
	private AuthSysModuleProvider authSysModuleProvider;
	
	@Autowired
	private GetwayBaseConfig getwayBaseConfig;

	/**
	 * 是否需要执行该filter，true表示执行，false表示不执行
	 */
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return (boolean) ctx.get("isSuccess");// 如果前一个过滤器的结果为true，则说明上一个过滤器成功了，需要进入当前的过滤，如果前一个过滤器的结果为false，则说明上一个过滤器没有成功，则无需进行下面的过滤动作了，直接跳过后面的所有过滤器并返回结果
	}

	/**
	 * 定义filter的顺序，数字越小表示顺序越高，越先执行
	 */
	@Override
	public int filterOrder() {
		return 2;
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

	private static final String NOFILTER = "notFilter", ISSUCCESS = "isSuccess";

	/**
	 * 
	 */
	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		// 不对地址进行过滤
		if (!ctx.getBoolean(NOFILTER)) {
			if ((boolean) ctx.get(ISSUCCESS)) {
				try {
					String authorization = (String) ctx.get("token");
					request.setAttribute("Authorization", authorization);
					JSONObject result = new JSONObject();
					//老服务会提供管辖部门与角色,新服务并不会提供角色和管辖部门
					String isOld = getwayBaseConfig.getIsOldService();
					if(!StringUtils.isEmpty(isOld) && Boolean.parseBoolean(isOld)) {
						result = authSysModuleProvider.checkUrlAuthIsOld(request.getRequestURI(), "true");
					}else {
						result = authSysModuleProvider.checkUrlAuth(request.getRequestURI());
					}
					if(1 != result.getInteger("code")) {
						// 过滤该请求，不对其进行路由
						ctx.setSendZuulResponse(Boolean.FALSE);
						// 返回错误码
						ctx.setResponseStatusCode(401);
						// 返回错误内容
						ctx.setResponseBody("{\"code\":401,\"result\":\"no authenticate info!\"}");
						ctx.set("isSuccess", Boolean.FALSE);
					}else {
						JSONObject rtnContent = result.getJSONObject("content");
						String moduleName = rtnContent.getString("cmodulenm");
						String operateLists = rtnContent.getString("operateLists");
						ctx.addZuulRequestHeader("AuthInfo", baseSixfourDecoder(JSON.toJSONString(rtnContent)));
						ctx.addZuulRequestHeader("Authorization", authorization);
						ctx.addZuulRequestHeader("zuulpath", request.getRequestURI());
						if(!StringUtils.isEmpty(moduleName)) {
							ctx.addZuulRequestHeader("moduleName",URLEncoder.encode(moduleName,"UTF-8"));
						}
						if(operateLists != null) {
							ctx.addZuulRequestHeader("operateLists", operateLists);
						}
						ctx.setSendZuulResponse(Boolean.TRUE);
						ctx.setResponseStatusCode(200);
						ctx.set("isSuccess", Boolean.TRUE);
					}
				} catch (Exception e) {
					ctx.setSendZuulResponse(Boolean.FALSE);
					ctx.setResponseStatusCode(200);
					ctx.setResponseBody("{\"code\":0,\"result\":\"getway exception please check getway server!\",\"msg\":\"" + e.getMessage() + "\"}");
					ctx.set("isSuccess", Boolean.FALSE);
				}
			}
		} else {
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
		}
		return null;
	}
	
	private String baseSixfourDecoder(String text) {
		final Base64.Encoder encoder = Base64.getEncoder();
		try {
			final byte[] textByte = text.getBytes("UTF-8");
			final String encodedText = encoder.encodeToString(textByte);
			return encodedText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
