package in.hocg.windmill.spring.boot.autoconfigure;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import in.hocg.windmill.spring.boot.autoconfigure.cache.AntiReplayCache;
import in.hocg.windmill.spring.boot.autoconfigure.handle.AntiReplayHandle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
public class AntiReplayFilter extends OncePerRequestFilter {
    
    private final AntiReplayCache cacheService;
    private final List<String> matchUrls;
    private final List<String> ignoreUrls;
    private final Long antiReplayInterval;
    
    @Getter
    private final AntiReplayHandle antiReplayHandle;
    
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String uri = request.getRequestURI();
        final String method = request.getMethod();
        
        // 是 GET 请求
        final boolean isGetRequest = HttpMethod.GET.name().equalsIgnoreCase(method);
        
        // 不满足匹配条件
        final boolean isNoMatchUrl = matchUrls.stream().noneMatch((pattern) -> antPathMatcher.match(pattern, uri));
        
        // 满足忽略条件不处理
        final boolean isIgnoreUrl = ignoreUrls.stream().anyMatch((url) -> antPathMatcher.match(url, uri));
        if (isGetRequest
                || isNoMatchUrl
                || isIgnoreUrl) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 处理需要防范的请求
        try {
            final RequestWrapper requestWrapper = new RequestWrapper(request);
            if (preHandle(requestWrapper)) {
                filterChain.doFilter(request, response);
            }
        } catch (AntiReplayException e) {
            AntiReplayHandle antiReplayHandle = getAntiReplayHandle();
            if (Objects.isNull(antiReplayHandle)) {
                log.error("请实现 {}, 否则无法处理重放错误信息.", AntiReplayHandle.class.getName());
                return;
            }
            antiReplayHandle.handle(request, response, e);
        }
    }
    
    public boolean preHandle(RequestWrapper servletWebRequest) {
        final String body = servletWebRequest.getBody();
        final String sign = servletWebRequest.getHeader(AntiReplayConstant.ANTI_REPLAY_PARAMETER_SIGN);
        String encode = Utils.sign(Utils.getOrDefault(body, ""));
        
        if (!encode.equals(sign)) {
            log.debug(String.format("请求[%s] != 服务端[%s] 签名错误, 请检查sign字段及加密策略", sign, encode));
            throw AntiReplayException.wrap("参数校验失败");
        }
        
        final JSONObject bodyJson = JSON.parseObject(body);
        final Long timestamp = bodyJson.getLong(AntiReplayConstant.ANTI_REPLAY_PARAMETER_TIMESTAMP);
        final String nonce = bodyJson.getString(AntiReplayConstant.ANTI_REPLAY_PARAMETER_NONCE);
        
        // sign, timestamp, nonce 必填
        if (Objects.isNull(timestamp)
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
        
        cacheService.put(nonceKey, currentTimeMillis, endExpiredTimeMillis, TimeUnit.MILLISECONDS);
        return true;
    }
    
}
