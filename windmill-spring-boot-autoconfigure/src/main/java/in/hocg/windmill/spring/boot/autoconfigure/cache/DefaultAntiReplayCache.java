package in.hocg.windmill.spring.boot.autoconfigure.cache;

import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAntiReplayCache implements AntiReplayCache {
    
    private final Cache<String, Long> cache;
    
    @Override
    public boolean contains(String nonceKey) {
        return cache.asMap().containsKey(nonceKey);
    }
    
    @Override
    public void put(String nonceKey, long currentTimeMillis, long expiredTimeMillis, TimeUnit milliseconds) {
        cache.put(nonceKey, currentTimeMillis);
    }
}
