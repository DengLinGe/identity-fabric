package scut.deng.didservice.pojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import scut.deng.didservice.pojo.constant.ErrorCode;

@Data
@Schema(name = "BaseResponse", title = "统一响应")
public class BaseResponse<T> {

    @Schema(title = "错误代码")
    private int code;


    @Schema(title = "信息")
    private String msg;

    @Schema(title = "数据")
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.data = data;
    }
    public static <T> BaseResponse<T> success(T data, String msg){
        BaseResponse<T> response = new BaseResponse<>(ErrorCode.SUCCESS, data);
        response.setMsg(msg);
        return response;
    }
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(ErrorCode.SUCCESS, data);
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<T>(ErrorCode.SUCCESS, (T) "");
    }

    public static <T> BaseResponse<T> failure(ErrorCode errorCode, Object... args) {
        String msg = "";
        if (args != null && args.length > 0) {
            msg = String.format(errorCode.getMsg(), args);
        } else {
            msg = errorCode.getMsg();
        }
        return new BaseResponse<>(errorCode.getCode(), msg);
    }
}
