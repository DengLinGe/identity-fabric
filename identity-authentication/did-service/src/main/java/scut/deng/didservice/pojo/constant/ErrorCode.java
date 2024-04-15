package scut.deng.didservice.pojo.constant;

public enum ErrorCode {

    SUCCESS(0, "操作成功"),
    REQ_ERROR(101, "请求参数校验失败"),
    NULL_ERROR(102, "空指针异常"),

    REQUEST_ERROR(103,"请求远程端口出错"),
    NO_DIDDOC(104,"不存在该did标识对应的文档"),

    ENC_ERROR(105, "加解密出现异常，请检查密钥对或者考虑是否遭遇篡改文档"),


    SYSTEM_ERROR(999,"未知异常");
    /**
     * 错误码
     */
    private int code;

    /**
     * 错误描述
     */
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
