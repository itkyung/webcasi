package com.kbsmc.webcasi.ui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.kbsmc.webcasi.ui.JSPTagUtil;

public class PreProcessFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		JSPTagUtil.init(filterConfig.getServletContext());
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		try {
			chain.doFilter(request, response);
		}
		finally {
			
		}

	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
