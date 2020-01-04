package in.hocg.windmill;

import in.hocg.windmill.spring.boot.autoconfigure.AntiReplayConstant;
import in.hocg.windmill.spring.boot.autoconfigure.Utils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Slf4j
public class SampleControllerTest extends AbstractSpringBootTest {
    
    @Test
    public void noHandleGet() throws Exception {
        URI url = uri().resolve("/no-handle");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(CoreMatchers.equalTo("SUCCESS"))
                );
    }
    
    @Test
    public void handleFail() throws Exception {
        URI url = uri().resolve("/handle");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(CoreMatchers.equalTo("FAIL"))
                );
    }
    
    @Test
    public void handleSuccess() throws Exception {
        URI url = uri().resolve("/handle");
        JSONObject postData = new JSONObject();
        long nonce = System.currentTimeMillis();
        long timestamp = System.currentTimeMillis();
        
        postData.put(AntiReplayConstant.ANTI_REPLAY_PARAMETER_NONCE, nonce);
        postData.put(AntiReplayConstant.ANTI_REPLAY_PARAMETER_TIMESTAMP, timestamp);
        final String content = postData.toJSONString();
        String encode = Utils.sign(content);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .content(content)
                .header(AntiReplayConstant.ANTI_REPLAY_PARAMETER_SIGN, encode)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(CoreMatchers.equalTo("SUCCESS"))
                );
    }
    
    @Test
    public void ignore() throws Exception {
        URI url = uri().resolve("/ignore/" + System.currentTimeMillis());
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(CoreMatchers.equalTo("SUCCESS"))
                );
    }
}
