package in.hocg.windmill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;


/**
 * @author hocgin
 * @date 2018/6/18
 * email: hocgin@gmail.com
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity handle(Exception e) throws Exception {
        log.error("处理异常信息", e);
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        
        String message = null;
        if (e instanceof BindException) {
            FieldError fieldError = ((BindException) e).getFieldError();
            if (Objects.nonNull(fieldError)) {
                message = fieldError.getDefaultMessage();
            }
        } else {
            message = e.getLocalizedMessage();
        }
        return Result.error(message).asResponseEntity();
    }
    
}
