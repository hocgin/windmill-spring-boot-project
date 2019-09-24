package in.hocg.windmill;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hocgin on 2019-09-24.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@RestController
public class SampleController {
    
    @GetMapping("/no-handle")
    public ResponseEntity<String> noHandle() {
        return ResponseEntity.ok("SUCCESS");
    }
    
    @PostMapping("/handle")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok("SUCCESS");
    }
    
    @PostMapping("/ignore/{i}")
    public ResponseEntity<String> ignore(@PathVariable("i") String i) {
        return ResponseEntity.ok("SUCCESS");
    }
}
