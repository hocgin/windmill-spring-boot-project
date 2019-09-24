package in.hocg.windmill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by hocgin on 2018/12/12.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Getter
@RequiredArgsConstructor
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "ok"),
    ERROR(500, "error");
    private final int code;
    private final String message;
}
