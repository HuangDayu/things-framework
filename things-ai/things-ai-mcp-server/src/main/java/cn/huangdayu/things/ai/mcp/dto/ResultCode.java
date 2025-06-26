package cn.huangdayu.things.ai.mcp.dto;


public enum ResultCode {

    SUCCESS(10000, "success"), FAILED(100001, "failed");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
