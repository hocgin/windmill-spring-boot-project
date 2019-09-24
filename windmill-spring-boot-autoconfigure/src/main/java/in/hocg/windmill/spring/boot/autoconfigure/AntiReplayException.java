package in.hocg.windmill.spring.boot.autoconfigure;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
public class AntiReplayException extends RuntimeException {
    public AntiReplayException(String message) {
        super(message);
    }
    
    public static AntiReplayException wrap(String message) {
        return new AntiReplayException(message);
    }
}
