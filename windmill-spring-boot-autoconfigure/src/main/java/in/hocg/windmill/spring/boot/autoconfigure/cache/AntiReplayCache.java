package in.hocg.windmill.spring.boot.autoconfigure.cache;

import java.util.concurrent.TimeUnit;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
public interface AntiReplayCache {
    
    boolean contains(String nonceKey);
    
    void put(String nonceKey, long currentTimeMillis, long endExpiredTimeMillis, TimeUnit milliseconds);
}
