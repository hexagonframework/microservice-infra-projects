package io.github.hexagonframework.microservice.infra.gateway.ratelimit;

import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.*;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.LimitRate;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitException;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitPolicy;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitPolicyKeyValue;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitProperties;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimiter;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

/**
 * @author Marcos Barbero
 * @author Michal Šváb
 * @author Xuegui Yuan
 */
@AllArgsConstructor
public class RateLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String X_DEVICE_ID = "X-DEVICE-ID";
    private static final String ANONYMOUS = "anonymous";

    private final RateLimiter limiter;
    private final RateLimitProperties properties;
    private final RouteLocator routeLocator;

    @Override
    public String filterType() {
        return "pre";
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
        Optional.ofNullable(policy()).ifPresent(rateLimitPolicyKeyValue -> {

            final LimitRate limitRate = this.limiter.consume(rateLimitPolicyKeyValue.getRateLimitPolicy(), key(request, rateLimitPolicyKeyValue.getKey(), rateLimitPolicyKeyValue.getRateLimitPolicy().getType()));
            response.setHeader(Headers.LIMIT, limitRate.getLimit().toString());
            response.setHeader(Headers.REMAINING, String.valueOf(Math.max(limitRate.getRemaining(), 0)));
            response.setHeader(Headers.RESET, limitRate.getReset().toString());
            if (limitRate.getRemaining() < 0) {
                ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                ctx.put("rateLimitExceeded", "true");
                throw new RateLimitException();
            }
        });
        return null;
    }

    /**
     * Get the requestURI from request.
     *
     * @return The request URI
     */
    private String requestURI() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
    }

    /**
     * Return the requestedContext from request.
     *
     * @return The requestedContext
     */
    private Route route() {
        return this.routeLocator.getMatchingRoute(this.requestURI());
    }

    private RateLimitPolicyKeyValue getFullPathPolicy(Route route) {
        if (route == null) {
            return null;
        }
        RateLimitPolicy rateLimitPolicy = this.properties.getPolicies().get(route.getFullPath());
        return rateLimitPolicy != null ? new RateLimitPolicyKeyValue(route.getFullPath(), rateLimitPolicy) : null;
    }

    private RateLimitPolicyKeyValue getRouteIdPolicy(Route route) {
        if (route == null) {
            return null;
        }
        RateLimitPolicy rateLimitPolicy = this.properties.getPolicies().get(route.getId());
        return (rateLimitPolicy != null) ? new RateLimitPolicyKeyValue(route.getId(), rateLimitPolicy) : null;
    }

    private RateLimitPolicyKeyValue policy() {
        Route route = route();
        RateLimitPolicyKeyValue routeFullPathPolicy = getFullPathPolicy(route);
        RateLimitPolicyKeyValue routeIdPolicy = getRouteIdPolicy(route);
        if (routeFullPathPolicy != null) {
            return routeFullPathPolicy;
        }
        if (routeIdPolicy != null) {
            return routeIdPolicy;
        }
        return null;
    }

    private String key(final HttpServletRequest request, final String key, final List<RateLimitPolicy.Type> types) {
        final Route route = route();
        final StringBuilder builder = new StringBuilder(key);
        if (types.contains(RateLimitPolicy.Type.URL)) {
            builder.append(":").append(route.getPath());
        }
        if (types.contains(RateLimitPolicy.Type.ORIGIN)) {
            builder.append(":").append(getRemoteAddr(request));
        }
        if (types.contains(RateLimitPolicy.Type.USER)) {
            builder.append(":").append((request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() :
                    ANONYMOUS);
        }
        if (types.contains(RateLimitPolicy.Type.DEVICE)) {
            builder.append(":").append(getDevivceId(request));
        }
        return builder.toString();
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        final String remoteAddr;
        if (this.properties.isBehindProxy() && request.getHeader(X_FORWARDED_FOR) != null) {
            remoteAddr = request.getHeader(X_FORWARDED_FOR);
        } else {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    private String getDevivceId(final HttpServletRequest request) {
        final String remoteAddr;
        if (this.properties.isBehindProxy() && request.getHeader(X_DEVICE_ID) != null) {
            remoteAddr = request.getHeader(X_DEVICE_ID);
        } else {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    interface Headers {
        String LIMIT = "X-RateLimit-Limit";
        String REMAINING = "X-RateLimit-Remaining";
        String RESET = "X-RateLimit-Reset";
    }
}
