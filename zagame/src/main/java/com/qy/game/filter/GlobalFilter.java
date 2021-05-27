package com.qy.game.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GlobalFilter implements Filter {


	@Override
	public void destroy() {

	}


	/**
	 * 解决跨域问题
	 *  (non-Javadoc)
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response=(HttpServletResponse) resp;
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		chain.doFilter(request, response);
	}
	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
