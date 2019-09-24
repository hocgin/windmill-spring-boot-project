package in.hocg.windmill.spring.boot.autoconfigure.handle;

import in.hocg.windmill.spring.boot.autoconfigure.AntiReplayException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
public interface AntiReplayHandle {
    
    void handle(HttpServletRequest request, HttpServletResponse response, AntiReplayException e);
}
