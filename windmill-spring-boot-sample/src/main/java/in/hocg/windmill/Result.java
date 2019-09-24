package in.hocg.windmill;

import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @author hocgin
 * @date 2017/10/14
 * email: hocgin@gmail.com
 * 响应结果对象
 */
@ToString
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;
    
    private Result() {
    }
    
    public static Result get() {
        return new Result();
    }
    
    public static Result result(boolean result) {
        return result ? success() : error();
    }
    
    public int getCode() {
        return code;
    }
    
    public Result setCode(int code) {
        this.code = code;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Result setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public Result setData(T data) {
        this.data = data;
        return this;
    }
    
    
    public static <T> Result<T> success(T data) {
        return Result.result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    
    public static <T> Result<T> success() {
        return Result.success(null);
    }
    
    public static <T> Result<T> result(Integer code, String message) {
        return Result.result(code, message, null);
    }
    
    public static <T> Result<T> error(String message) {
        return Result.result(ResultCode.ERROR.getCode(), message, null);
    }
    
    public static <T> Result<T> error() {
        return Result.result(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), null);
    }
    
    public static <T> Result<T> result(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        return result.setCode(code)
                .setMessage(message)
                .setData(data);
    }
    
    public ResponseEntity<Result<T>> asResponseEntity() {
        return ResponseEntity.ok(this);
    }
    
}
