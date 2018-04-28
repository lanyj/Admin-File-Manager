package cn.lanyj.am.authentication;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * 增加方法注入，将含有 CurrentUser 注解的方法参数注入当前登录用户
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
    public boolean supportsParameter (MethodParameter parameter) {
        // 如果参数类型是 User 并且有 CurrentUser 注解则支持
		
		// 为了简化使用，在resolveArgument中判断是不是AuthenticationUser.class，如果是，就返回。不是则返回user.getObject()
		// && parameter.getParameterType ().isAssignableFrom (AuthenticationUser.class)
        if (parameter.hasParameterAnnotation (CurrentUser.class)) {
            return true;
        }
        return false;
    }
    @Override
    public Object resolveArgument (MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 取出鉴权时存入的登录用户 Id
    	AuthenticationUser user = (AuthenticationUser) webRequest.getAttribute(Authentication.AUTHEN_USER, RequestAttributes.SCOPE_REQUEST);
    	
        if (user != null) {
        	if(parameter.getParameterType().isAssignableFrom(AuthenticationUser.class)) {
        		return user;
        	}
        	// return dynamic type, which method needed.
        	if(user.getObject() != null) {
        		if(user.getObject().getClass().isAssignableFrom(parameter.getParameterType())) {
        			return user.getObject();
        		} else {
        			throw new IllegalArgumentException("Need argument's type is '" + parameter.getParameterType().getName() + "', but offered is '" + user.getObject().getClass().getName() + "'");
        		}
        	}
        }
        throw new MissingServletRequestPartException (Authentication.AUTHEN_USER);
    }
}