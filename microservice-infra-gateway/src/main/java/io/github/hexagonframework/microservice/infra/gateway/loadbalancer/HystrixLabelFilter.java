package io.github.hexagonframework.microservice.infra.gateway.loadbalancer;

import com.jkgj.skykingkong.loadbalancer.HystrixLabelRequestContext;
import io.github.hexagonframework.microservice.infra.gateway.utils.IpUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 拦截请求获取访问端label用于负载访问的HTTP服务器.
 *
 * @author Xuegui Yuan
 */
public class HystrixLabelFilter extends ZuulFilter {
  private static final String X_LABEL = "X-LABEL";
  private static final String X_DEVICE_ID = "X-DEVICE-ID";
  private static final String TEST_DEVICE_ID_KEY = "test_device_id";
//  private static final String TSET_DEVICE_IP_KEY = "test_device_ip";
  private final StringRedisTemplate redisTemplate;

  private static final Logger logger = LoggerFactory.getLogger(HystrixLabelFilter.class);

  public HystrixLabelFilter(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    String ip = IpUtils.getIpAddress(ctx.getRequest());
    String deviceId = ctx.getRequest().getHeader(X_DEVICE_ID);
    String headerLabels = ctx.getRequest().getHeader(X_LABEL);
    logger.debug("Header {}:{}", X_DEVICE_ID, deviceId);
    logger.debug("Header {}:{}", X_LABEL, headerLabels);
    List<String> labels = new ArrayList<>();
    if (StringUtils.isNotBlank(headerLabels)) {
      labels.addAll(Arrays.asList(headerLabels.split(",")));
    }

    boolean isTestDevice = false;
    logger.debug("test_device_id: {}", redisTemplate.boundSetOps(TEST_DEVICE_ID_KEY).members());
//    logger.debug("test_device_ip: {}", redisTemplate.boundSetOps(TSET_DEVICE_IP_KEY).members());
    if (StringUtils.isNotBlank(deviceId)) {
      isTestDevice = redisTemplate.boundSetOps(TEST_DEVICE_ID_KEY).isMember(deviceId);
    }
//    if (IpUtils.isInternalIp(ip)
//        || redisTemplate.boundSetOps(TSET_DEVICE_IP_KEY).isMember(ip)) {
//      isTestDevice = true;
//    }
    if (isTestDevice) {
      labels.add("test");
    }
    logger.info("HystrixLabelFilter {}", labels);

    /**
     * zuul本身调用微服务
     */
    HystrixLabelRequestContext.initHystrixRequestContext(labels);
    /**
     * 传递给后续微服务
     */
    ctx.addZuulRequestHeader(HystrixLabelRequestContext.HEADER_LABEL, StringUtils.join(labels, ","));

    return null;
  }
}
