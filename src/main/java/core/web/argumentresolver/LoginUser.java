package core.web.argumentresolver;

import java.lang.annotation.*;

/**
 * 로그인 사용자 웹 객체를 컨트롤러의 인자로 받게 해준다.
 * <p>
 * {@link LoginUserHandlerMethodArgumentResolver}가 사용한다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
}
