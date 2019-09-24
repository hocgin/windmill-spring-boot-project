package in.hocg.windmill.spring.boot.autoconfigure;

import in.hocg.windmill.spring.boot.autoconfigure.handle.AntiReplayHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author hocgin
 * @date 18-8-20
 **/
@Slf4j
public abstract class SimpleHandlerFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    
        RequestWrapper webRequest = new RequestWrapper(request);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(AntiReplayConstant.UTF_8);
        
        try {
            if (preHandle(webRequest)) {
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
    
    /**
     * 简单的复写 preHandle
     *
     * @param servletWebRequest
     * @return
     */
    abstract boolean preHandle(RequestWrapper servletWebRequest);
    
    /**
     * 防重放处理器
     *
     * @return
     */
    abstract AntiReplayHandle getAntiReplayHandle();
}
