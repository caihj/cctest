package com.berbon.jfaccount.commen;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.utils.Constant;
import com.berbon.jfaccount.utils.MyUtils;
import com.berbon.jfaccount.vo.DataResponse;
import com.berbon.msgsrv.proxy.facade.MsgSrvProxyFacade;
import com.berbon.msgsrv.proxy.pojo.ProxyRequestParam;
import com.berbon.user.pojo.Users;
import com.berbon.util.String.StringUtil;
import com.berbon.util.base64.Base64;
import com.pay1pay.framework.core.exception.BusinessRuntimeException;

/**
 * 过滤器 判断是否登录
 * 
 * @author baijt
 *
 *         2015年7月28日
 */
public class CheckLoginInterceptor extends HandlerInterceptorAdapter {
	static Logger logger = LoggerFactory.getLogger(CheckLoginInterceptor.class);

	@Autowired
	private Constant constant;
	@Autowired
	private MsgSrvProxyFacade msgSrvProxyFacade;

	@Override
	public  boolean    preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		try {
			response.setContentType("text/html;charset=UTF-8");
			String sessionId = getCookie(request, constant.getBerbonsessionId());
			if (sessionId == null) {
				throw new BusinessRuntimeException("", "");
			}
			String packetId = StringUtil.getSystemUniqueNo();
			String msgsrvCmd = "SessionAdminBerBon getSessionAtt NA." + packetId + " " + sessionId + " @BerbonSessClt.AgentLogin @BerbonSessClt.user";
			String result = msgSrvProxyFacade.getResult(new ProxyRequestParam(5, packetId, 2, msgsrvCmd));
			System.err.println(sessionId);
			String[] results = result.split(" +");
			String requestURL = request.getRequestURL().toString();
			if (requestURL.contains("?")) {
				requestURL = requestURL.split("\\?")[0];
			}
			logger.info("");
			if ("1".equals(results[3])) {
				String loginState = Base64.decode(results[4], "GBK");
				if (!"true".equals(loginState)) {
					if (!(requestURL.contains("fuelcard/index.htm")||requestURL.contains("mobiledata/index.htm")||requestURL.contains("broadband/index.htm")||requestURL.contains("007bao/index.htm")||requestURL.contains("report/index.htm")||requestURL.contains("mobilebatch/index.htm")||requestURL.contains("etcinfo/index.htm")||requestURL.contains("sztcard/index.htm"))) {
						response.getWriter().write(MyUtils.getJsonP(request,JSONObject.toJSONString(new DataResponse(MyErrorCodeEnum.not_login.getErrCode(), MyErrorCodeEnum.not_login.getOutDesc(), null))));
						return false;
					}else {
						throw new BusinessRuntimeException("","");
					}
				}
				String userString = Base64.decode(results[5], "GBK");
				logger.info("user---------》："+userString);
				request.getSession().setAttribute("user", userString);
				return true;
			}
		} catch (BusinessRuntimeException e) {
			logger.info("跳转地址："+constant.getRedictUrl());
			response.sendRedirect(constant.getRedictUrl());
		}
		
		return false;
	}
	
/*	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		
		MyHttpServletResponseWrapper httpServletResponseWrapper = new MyHttpServletResponseWrapper(response);
		
		   logger.info("................"+httpServletResponseWrapper.toString());
		
	}*/

	public static Users getUsers(HttpSession httpSession) {
		
		String userString = (String) httpSession.getAttribute("user");
		Users users = null;
		if (userString != null) {
			users = JSONObject.parseObject(userString, Users.class);
		}
		return users;
	}

	/**
	 * 
	 * @param sessionId
	 * @return
	 */
//	public static String getDistruce(String sessionId) {
//		Map<String, Object> oldSessionMap = BerbonSessionUtil.getAttributeMap(sessionId, "AgentInnerSalt");
//		String salt = (String) oldSessionMap.get("AgentInnerSalt");
//		if (salt != null) {
//			BerbonSessionUtil.removeAttribute(sessionId, "AgentInnerSalt");
//		}
//		return salt;
//	}
	
	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public static String getDistruce(HttpSession session) {
		String saltAttributeName="AgentInnerSalt";
		String salt =(String) session.getAttribute(saltAttributeName);
		session.removeAttribute(saltAttributeName);
		return salt;
	}

	public String getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(name)) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}

}
