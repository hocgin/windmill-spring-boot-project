package in.hocg.windmill;

import in.hocg.windmill.spring.boot.autoconfigure.AntiReplayException;
import in.hocg.windmill.spring.boot.autoconfigure.handle.AntiReplayHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Slf4j
@Component
public class DefaultAntiReplayHandle implements AntiReplayHandle {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AntiReplayException e) {
        log.error("处理错误信息", e);
        try {
            response.getWriter().write("FAIL");
        } catch (IOException ignored) {
        }
    }
}
