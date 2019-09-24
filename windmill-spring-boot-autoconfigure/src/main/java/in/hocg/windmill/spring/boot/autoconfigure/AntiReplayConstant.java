package in.hocg.windmill.spring.boot.autoconfigure;

import lombok.experimental.UtilityClass;

/**
 * @author hocgin
 * @date 18-8-20
 * 防重放攻击配置
 **/
@UtilityClass
public class AntiReplayConstant {
    public static final String UTF_8 = "UTF-8";
    public static final String ANTI_REPLAY_PARAMETER_SIGN = "sign";
    public static final String ANTI_REPLAY_PARAMETER_TIMESTAMP = "timestamp";
    public static final String ANTI_REPLAY_PARAMETER_NONCE = "nonce";
    public static final int ANTI_REPLAY_INTERVAL = 60 * 1000;
}
