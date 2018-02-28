package io.github.hexagonframework.microservice.infra.gateway.errorlimit;

import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimitProperties;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimiter;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.*;
import com.netflix.zuul.context.RequestContext;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimitException;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorRate;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


/**
 * @author Xuegui Yuan
 */
public class ErrorLimitPostFilter extends AbstractErrorLimitFilter {

    private final ErrorLimiter limiter;

    public ErrorLimitPostFilter(ErrorLimiter limiter, ErrorLimitProperties properties, RouteLocator routeLocator) {
        super(properties, routeLocator);
        this.limiter = limiter;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return this.properties.isEnabled() && policy() != null;
    }

    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletResponse response = ctx.getResponse();
        final HttpServletRequest request = ctx.getRequest();
        Optional.ofNullable(policy()).ifPresent(policyKeyValue -> {
            if (hasError(response)){ // has error

                final ErrorRate errorRate = this.limiter.consume(policyKeyValue.getErrorLimitPolicy(), key(request, policyKeyValue.getKey(), policyKeyValue.getErrorLimitPolicy().getType()));
                response.setHeader(Headers.LIMIT, errorRate.getLimit().toString());
                response.setHeader(Headers.REMAINING, String.valueOf(Math.max(errorRate.getRemaining(), 0)));
                response.setHeader(Headers.RESET, errorRate.getReset().toString());
                if (errorRate.getRemaining() < 0) {
                    ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                    ctx.put("errorLimitExceeded", "true");
                    throw new ErrorLimitException();
                }
            }
        });
        return null;
    }

    private boolean hasError(HttpServletResponse response) {
        return response.getStatus() == 400;
    }

}
