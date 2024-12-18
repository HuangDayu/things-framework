package cn.huangdayu.things.common.enums;

/**
 * @author huangdayu
 */
public enum ThingsMethodType {
    SYSTEM,
    SERVICE,
    EVENT,
    PROPERTY,
    ALL_METHOD;

    public static ThingsMethodType getMethodType(String methodType) {
        for (ThingsMethodType type : ThingsMethodType.values()) {
            if (type.name().equalsIgnoreCase(methodType)) {
                return type;
            }
        }
        return ALL_METHOD;
    }

}
