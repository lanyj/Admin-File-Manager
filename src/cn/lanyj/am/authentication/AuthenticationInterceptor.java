package cn.lanyj.am.authentication;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "*");
		response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.addHeader("Content-type", "application/json");

		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		String key = request.getRemoteHost();

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		{
			if (Authentication.isAuthentication(key)) {
				Authentication authentication = Authentication.getAuthentication(key);
				AuthenticationUser user = authentication.getUser();
				request.setAttribute(Authentication.AUTHEN_USER, user);
			}
		}
		// 验证 key，方法注明了 Authorization，返回 401 错误
		if (method.getAnnotation(Authorization.class) != null) {
			Authorization authorization = method.getAnnotation(Authorization.class);
			Level level = authorization.level();

			if (level == Level.ANONYMOUS) {
				return true;
			} else if (Authentication.isAuthentication(key)) {
				Authentication authentication = Authentication.getAuthentication(key);
				AuthenticationUser user = authentication.getUser();

				if (level == Level.USER) {
					return true;
				} else if (level == Level.ADMIN) {
					return user.getLevel() == Level.ADMIN;
				}
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		return true;
	}

}
