package in.hocg.windmill.spring.boot.autoconfigure;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@UtilityClass
public class Utils {
    
    public static String sign(String paramStr) {
        return Hashing.md5().newHasher().putString(paramStr, Charsets.UTF_8).hash().toString();
    }
    
    public static <T> T getOrDefault(T v, T def) {
        if (Objects.isNull(v)) {
            return def;
        }
        return v;
    }
}
