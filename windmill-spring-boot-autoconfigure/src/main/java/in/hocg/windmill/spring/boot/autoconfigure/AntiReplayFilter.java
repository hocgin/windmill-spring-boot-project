package in.hocg.windmill.spring.boot.autoconfigure;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import in.hocg.windmill.spring.boot.autoconfigure.cache.AntiReplayCache;
import in.hocg.windmill.spring.boot.autoconfigure.handle.AntiReplayHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import javax.servlet.annotation.WebFilter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author hocgin
 * @date 18-8-20
 * 防重放攻击
 **/
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@WebFilter(filterName = "AntiReplayFilter", urlPatterns = {"/*"})
public class AntiReplayFilter extends SimpleHandlerFilter {
    
    private final AntiReplayCache cacheService;
    private final List<String> ignoreUrls;
    private final Long antiReplayInterval;
    private final AntiReplayHandle antiReplayHandle;
    
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    @Override
    public boolean preHandle(RequestWrapper servletWebRequest) {
        String requestURI = servletWebRequest.getRequestURI();
        
        /*
        - GET 请求不处理
        - 满足匹配条件不处理
         */
        if (HttpMethod.GET.name().equalsIgnoreCase(servletWebRequest.getMethod())
                || ignoreUrls.parallelStream().anyMatch((url) -> antPathMatcher.match(url, requestURI))) {
            return true;
        }
        
        Map<String, Object> parameterMap = getParams(servletWebRequest);
        
        String sign = getSingleValue(parameterMap, AntiReplayConstant.ANTI_REPLAY_PARAMETER_SIGN);
        String timestampStr = getSingleValue(parameterMap, AntiReplayConstant.ANTI_REPLAY_PARAMETER_TIMESTAMP);
        String nonce = getSingleValue(parameterMap, AntiReplayConstant.ANTI_REPLAY_PARAMETER_NONCE);
        Long timestamp = Objects.isNull(timestampStr) ? 0L : Long.parseLong(timestampStr);
        
        // sign, timestamp, nonce 必填
        if (Objects.isNull(sign)
                || Objects.isNull(nonce)) {
            log.debug(String.format("必填: sign(%s), timestamp(%s), nonce(%s)", sign, timestamp, nonce));
            
            throw AntiReplayException.wrap("参数校验失败");
        }
        
        // 验证timestamp
        long currentTimeMillis = System.currentTimeMillis();
        long startExpiredTimeMillis = currentTimeMillis - antiReplayInterval;
        long endExpiredTimeMillis = currentTimeMillis + antiReplayInterval;
        if (timestamp < startExpiredTimeMillis
                || timestamp > endExpiredTimeMillis) {
            log.debug(String.format("时间戳为%d, 检查范围: (%d, %d)", timestamp, startExpiredTimeMillis, endExpiredTimeMillis));
            throw AntiReplayException.wrap("参数校验失败");
        }
        
        // 验证 nonce
        String nonceKey = String.format("%s.%s", AntiReplayConstant.ANTI_REPLAY_PARAMETER_NONCE, nonce);
        if (cacheService.contains(nonceKey)) {
            log.debug(String.format("nonce[%s] 请检查nonce参数", nonce));
            throw AntiReplayException.wrap("参数校验失败");
        }
        
        // 验证 sign
        String[] keys = parameterMap.keySet()
                .toArray(new String[]{});
        
        // 格式: k=v&k=v&k=v, md5(k=v&k=v&k=v)
        Optional<String> str = Arrays.stream(keys)
                .filter(s -> !AntiReplayConstant.ANTI_REPLAY_PARAMETER_SIGN.equals(s))
                .sorted()
                .map(key -> String.format("%s=%s", key, parameterMap.get(key)))
                .reduce((k1, k2) -> String.format("%s&%s", k1, k2));
        String encode = Utils.sign(str.orElse(""));
        if (!sign.equals(encode)) {
            log.debug(String.format("请求[%s] != 服务端[%s] 签名错误, 请检查sign字段及加密策略", sign, encode));
            throw AntiReplayException.wrap("参数校验失败");
        }
        
        cacheService.put(nonceKey, currentTimeMillis, endExpiredTimeMillis, TimeUnit.MILLISECONDS);
        return true;
    }
    
    private Map<String, Object> getParams(RequestWrapper requestWrapper) {
        HashMap<String, Object> paramsMap = Maps.newHashMap();
        
        Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
        String body = requestWrapper.getBody();
        Map<String, String[]> bodyMap = JSON.parseObject(body, Map.class);
        paramsMap.putAll(parameterMap);
        paramsMap.putAll(bodyMap);
        return paramsMap;
    }
    
    @Override
    AntiReplayHandle getAntiReplayHandle() {
        return this.antiReplayHandle;
    }
    
    private String getSingleValue(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        
        Object result;
        if (value.getClass().isArray()) {
            result = ((Object[]) value)[0];
        } else {
            result = value;
        }
        
        return String.valueOf(result);
    }
    
}
