package in.hocg.windmill.spring.boot.autoconfigure.properties;

import com.google.common.collect.Lists;
import in.hocg.windmill.spring.boot.autoconfigure.AntiReplayConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Data
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@ConfigurationProperties(prefix = WindmillProperties.PREFIX)
public class WindmillProperties {
    public static final String PREFIX = "spring.windmill";
    
    /**
     * 防重放的间隔时间, 默认: 60s
     */
    private Long antiReplayInterval = AntiReplayConstant.ANTI_REPLAY_INTERVAL;
    
    /**
     * 匹配的 URL(默认所有)
     */
    private List<String> matchUrl = Lists.newArrayList("/**");
    
    /**
     * 忽略的 URL
     */
    private List<String> ignoreUrl = Lists.newArrayList();
    
    
}
