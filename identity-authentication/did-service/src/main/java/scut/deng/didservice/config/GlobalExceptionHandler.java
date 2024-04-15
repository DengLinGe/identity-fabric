package scut.deng.didservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.pojo.constant.ErrorCode;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数校验不通过请求
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        try {
            // 获取所有属性的校验错误
            List<ObjectError> fieldErrorList = ex.getBindingResult().getAllErrors();
            log.warn("request field validation failed {}", fieldErrorList);
            if (fieldErrorList.isEmpty()) {
                log.warn("request field validation failed");
                return BaseResponse.failure(ErrorCode.REQ_ERROR);
            }
            // 拼装属性校验失败具体信息
            StringJoiner stringJoiner = new StringJoiner(";");
            fieldErrorList.forEach(fieldError -> stringJoiner.add(fieldError.getDefaultMessage()));
            log.warn("request API:{} validation failed {}", request.getRequestURI(), stringJoiner);
            return new BaseResponse<>(ErrorCode.REQ_ERROR, stringJoiner.toString());
        } catch (Exception e) {
            log.error("request field validation failed", e);
            return BaseResponse.failure(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 处理请求参数校验不通过异常
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        ex.printStackTrace();
        Set<ConstraintViolation<?>> constraints = ex.getConstraintViolations();
        Optional<ConstraintViolation<?>> optionalConstraints = constraints.stream().findFirst();
        System.out.println("1111111111111111111111111111111111111111111111");
        return optionalConstraints.map(
                        constraintViolation -> new BaseResponse(ErrorCode.REQ_ERROR, constraintViolation.getMessage()))
                .orElseGet(() -> BaseResponse.failure(ErrorCode.SYSTEM_ERROR));

    }



    /**
     * 默认异常处理
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse handleDefaultException(HttpServletRequest request, Exception ex) {
        log.error("System Exception! request API:{},Exception:{}", request.getRequestURI(), ex.toString());
        ex.printStackTrace();
        if (ex instanceof NullPointerException) {
            return BaseResponse.failure(ErrorCode.NULL_ERROR);
        } else {
            return BaseResponse.failure(ErrorCode.SYSTEM_ERROR);
        }
    }
    @ExceptionHandler(MyException.class)
    public BaseResponse handleMyException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return new BaseResponse(999, ex.getMessage());

    }
}
