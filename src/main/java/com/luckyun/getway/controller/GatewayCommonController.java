package com.luckyun.getway.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.luckyun.getway.Locator.ConfigSystemLocator;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/gateway/common")
@Slf4j
public class GatewayCommonController {

	@Autowired
	private ConfigSystemLocator configSystemLocator;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/noGetwayRecycle")
	public List<JSONObject> getSysRecycleServer(HttpServletRequest request) {
		String host = request.getHeader("Host");
		List<String> routeList =  configSystemLocator.everyMappingPath();
		List<JSONObject> showRecycle = new ArrayList<JSONObject>();
		if(routeList != null && routeList.size() >= 1) {
			final CountDownLatch countDownLatch = new CountDownLatch(routeList.size());
			for(String pre : routeList) {
				new Thread(new WorkRunable(countDownLatch,showRecycle, pre, host)).start();
			}
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return showRecycle;
	}
	
	@GetMapping("getRoutePre")
	public String getRoutePre(@RequestParam("serverId") String serverId){
		List<Route> routeList =  configSystemLocator.everyMappingRoute();
		for(Route route : routeList) {
			if(route.getLocation().equals(serverId)) {
				return route.getId();
			}
		}
		return "";
	}
	
	public class WorkRunable implements Runnable{

		private CountDownLatch countDownLatch;
		
		private List<JSONObject> showRecycle;
		
		private String pre;
		
		private String host;
		
		public WorkRunable(CountDownLatch countDownLatch,List<JSONObject> preList,String pre,String host) {
			this.countDownLatch = countDownLatch;
			this.showRecycle = preList;
			this.pre = pre;
			this.host = host;
		}
		
		@Override
		public void run() {
			try {
				ResponseEntity<JSONObject> state = 
						restTemplate.getForEntity("http://"+host+"/"+pre + "/recycle/common/noGetwaySystem", JSONObject.class);
				JSONObject jsonObject = state.getBody();
				if(state.getStatusCode().equals(HttpStatus.OK) 
						&& jsonObject.getInteger("showRecycle") != 0) {
					jsonObject.put("pre", pre);
					showRecycle.add(jsonObject);
				}
			}catch (Exception e){
				log.error("服务不存在,无法请求回收站接口");
			}finally {
				countDownLatch.countDown();
			}
		}
		
	}
}
