package in.hocg.windmill;

import in.hocg.windmill.spring.boot.autoconfigure.AntiReplayConstant;
import in.hocg.windmill.spring.boot.autoconfigure.Utils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Slf4j
public class SampleControllerTest extends AbstractSpringBootTest {
    
    @Test
    public void noHandle() {
        URI uri = uri().resolve("/no-handle");
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("SUCCESS", response.getBody());
    }
    
    @Test
    public void handleFail() {
        URI uri = uri().resolve("/handle");
        JSONObject postData = new JSONObject();
        ResponseEntity<String> response = restTemplate.postForEntity(uri, postData, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("FAIL", response.getBody());
    }
    
    @Test
    public void handleSuccess() {
        URI uri = uri().resolve("/handle");
        JSONObject postData = new JSONObject();
        long nonce = System.currentTimeMillis();
        long timestamp = System.currentTimeMillis();
        
        postData.put(AntiReplayConstant.ANTI_REPLAY_PARAMETER_NONCE, nonce);
        postData.put(AntiReplayConstant.ANTI_REPLAY_PARAMETER_TIMESTAMP, timestamp);
        String signParam = postData.keySet().stream().sorted()
                .map(key -> String.format("%s=%s", key, postData.getAsString(key)))
                .reduce((k1, k2) -> String.format("%s&%s", k1, k2)).orElse("");
        String encode = Utils.sign(signParam);
        
        postData.put(AntiReplayConstant.ANTI_REPLAY_PARAMETER_SIGN, encode);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, postData, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("SUCCESS", response.getBody());
    }
    
    @Test
    public void ignore() {
        URI uri = uri().resolve("/ignore/" + System.currentTimeMillis());
        JSONObject postData = new JSONObject();
        ResponseEntity<String> response = restTemplate.postForEntity(uri, postData, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals("SUCCESS", response.getBody());
    }
}
