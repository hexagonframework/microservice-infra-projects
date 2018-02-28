package io.github.hexagonframework.microservice.infra.gateway.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Xuegui Yuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult {
    private String errCode;
    private String errMessage;

    public static final String NOT_FOUND_ERR_CODE = "410001";
    public static final String TOO_MANY_REQUESTS_ERR_CODE = "410002";

    public static final String NOT_FOUND_ERR_MESSAGE = "服务未找到";
    public static final String TOO_MANY_REQUESTS_ERR_MESSAGE = "请求频次超过上限";

    public static ErrorResult ErrorResultFromHttpStatus(int httpStatus) {
        if (httpStatus == 404) {
            return new ErrorResult(NOT_FOUND_ERR_CODE, NOT_FOUND_ERR_MESSAGE);
        }
        else if (httpStatus == 429) {
            return new ErrorResult(TOO_MANY_REQUESTS_ERR_CODE, TOO_MANY_REQUESTS_ERR_MESSAGE);
        }
        else {
            return new ErrorResult("410" + httpStatus, HttpStatus.valueOf(httpStatus).getReasonPhrase());
        }
    }

    public static String errCodeFromHttpStatus(int httpStatus) {
        if (httpStatus == 404) {
            return NOT_FOUND_ERR_CODE;
        }
        else if (httpStatus == 429) {
            return TOO_MANY_REQUESTS_ERR_CODE;
        }
        else {
            return "410" + httpStatus;
        }
    }

    public static String errMessageFromHttpStatus(int httpStatus) {
        if (httpStatus == 400) {
            return TOO_MANY_REQUESTS_ERR_CODE;
        }
        else if (httpStatus == 429) {
            return TOO_MANY_REQUESTS_ERR_MESSAGE;
        }
        else {
            return HttpStatus.valueOf(httpStatus).getReasonPhrase();
        }
    }
}
