package io.github.hexagonframework.microservice.infra.gateway.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Xuegui Yuan
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 404异常处理
     *
     * @param req
     * @param e
     * @return ErrorResult
     * @throws Exception
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResult defaultNoHandlerFoundExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        logger.error(e.getMessage());
        return ErrorResult.ErrorResultFromHttpStatus(404);
    }
}

