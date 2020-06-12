package com.luckyun.getway.Locator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

public class ConfigSystemLocator extends SimpleRouteLocator{

	public ConfigSystemLocator(String servletPath, ZuulProperties properties) {
		super(servletPath, properties);
	}
	
	public List<String> everyMappingPath(){
		List<Route> routes= super.getRoutes();
		List<String> routeIds = new ArrayList<String>(routes.size());
		for(Route route :routes) {
			routeIds.add(route.getId());
		}
		return routeIds;
	}
	
	public List<Route> everyMappingRoute(){
		return super.getRoutes();
	}
}
