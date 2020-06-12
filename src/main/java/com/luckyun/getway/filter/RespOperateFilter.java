package com.luckyun.getway.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 返回结果集添加操作列表数据，后续记录错误日志
 * 
 * 2019年05月22日,上午10:30 
 * {@link com.lucksoft.getway.filter.RespOperateFilter}
 * 
 * @author yangj080
 * @version 1.0.0
 *
 */
public class RespOperateFilter  extends ZuulFilter{
	
	@Autowired
	private ThreadLocal<List<String>> threadLocal; 

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		
		InputStream stream = ctx.getResponseDataStream();
		try {
			if(stream != null) {
				String result = StreamUtils.copyToString(stream,Charset.forName("UTF-8"));
				JSONObject rtnResult = JSON.parseObject(result);
				if(rtnResult != null) {
					rtnResult.put("operates", threadLocal.get());
				}
				ctx.setSendZuulResponse(true);
				ctx.setResponseStatusCode(200);
				ctx.setResponseBody(rtnResult.toJSONString());
			}
		} catch (Exception e) {
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody("{\"code\":0,\"msg\":\"getway convert exception,please check getway server\"}");
		} finally {
			//释放内存
			threadLocal.remove();
			try {
				if(stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String filterType() {
		//返回结果过滤器
		return "post";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
