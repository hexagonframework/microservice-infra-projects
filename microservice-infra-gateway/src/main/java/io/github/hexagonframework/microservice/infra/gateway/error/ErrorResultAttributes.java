package io.github.hexagonframework.microservice.infra.gateway.error;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.ServletException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Xuegui Yuan
 */
public class ErrorResultAttributes extends DefaultErrorAttributes {

    private static final String ERROR_ATTRIBUTE = ErrorResultAttributes.class.getName()
            + ".ERROR";

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                                                  boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
        addErrCode(errorAttributes, requestAttributes);
        addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
        return errorAttributes;
    }

    private void addErrCode(Map<String, Object> errorAttributes,
                           RequestAttributes requestAttributes) {
        Integer status = getAttribute(requestAttributes,
                "javax.servlet.error.status_code");
        Throwable error = getError(requestAttributes);
        if (error != null) {
            if ("RateLimitException".equals(error.getCause())) {
                errorAttributes.put("errCode", "410002");
                return;
            }
            else if ("ErrorLimitException".equals(error.getCause())) {
                errorAttributes.put("errCode", "410003");
                return;
            }
        }

        if (status == null) {
            errorAttributes.put("errCode", "999");
            return;
        }
        errorAttributes.put("errCode", ErrorResult.errCodeFromHttpStatus(status));
    }

    private void addErrorDetails(Map<String, Object> errorAttributes,
                                 RequestAttributes requestAttributes, boolean includeStackTrace) {
        Throwable error = getError(requestAttributes);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
            addErrorMessage(errorAttributes, error);
        }
        Object message = getAttribute(requestAttributes, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(message) && errorAttributes.get("errMessage") == null)
                && !(error instanceof BindingResult)) {
            errorAttributes.put("errMessage",
                    StringUtils.isEmpty(message) ? "No message available" : message);
        }
    }

    private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            errorAttributes.put("errMessage", error.getMessage());
            return;
        }
        if (result.getErrorCount() > 0) {
            errorAttributes.put("errors", result.getAllErrors());
            errorAttributes.put("errMessage",
                    "Validation failed for object='" + result.getObjectName()
                            + "'. Error count: " + result.getErrorCount());
        }
        else {
            errorAttributes.put("errMessage", "No errors");
        }
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }

    @Override
    public Throwable getError(RequestAttributes requestAttributes) {
        Throwable exception = getAttribute(requestAttributes, ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(requestAttributes, "javax.servlet.error.exception");
        }
        return exception;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
