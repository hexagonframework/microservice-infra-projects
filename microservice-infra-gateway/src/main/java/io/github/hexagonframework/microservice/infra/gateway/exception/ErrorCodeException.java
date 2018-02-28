package io.github.hexagonframework.microservice.infra.gateway.exception;

import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;

/**
 * @author Xuegui Yuan
 */
public abstract class ErrorCodeException extends ZuulRuntimeException {
    private String errCode;

    public ErrorCodeException(String sMessage, int nStatusCode, String errorCause, String errCode) {
        super(new ZuulException(sMessage, nStatusCode, errorCause));
        this.errCode = errCode;
    }

    public ErrorCodeException(ZuulException cause) {
        super(cause);
    }

    public ErrorCodeException(Exception ex) {
        super(ex);
    }

    public String getErrCode() {
        return this.errCode;
    }
}
