package cn.huangdayu.things.engine.wrapper;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

import static cn.huangdayu.things.engine.common.ThingsConstants.THINGS_SEPARATOR;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ThingsInstance {

    /**
     * 实例标识
     */
    private String code;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 协议
     *
     * @see cn.huangdayu.things.engine.common.ThingsConstants.Protocol
     */
    private String protocol;


    /**
     * 有会话的
     */
    private int sessions;

    /**
     * 服务地址
     */
    private String server;


    /**
     * 提供的物模型的productCode
     */
    private Set<String> provides;


    /**
     * 消费的物模型的productCode
     * 消费的服务，订阅的事件
     */
    private Set<String> consumes;


    /**
     * 订阅列表
     */
    private Set<String> subscribes;


    public String getCode() {
        return name + THINGS_SEPARATOR + protocol + THINGS_SEPARATOR + server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThingsInstance that = (ThingsInstance) o;
        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    @Override
    public String toString() {
        return getCode();
    }

    public static ThingsInstance valueOf(String value) {
        ThingsInstance thingsInstance = new ThingsInstance();
        if (StrUtil.isNotBlank(value)) {
            String[] split = value.split(THINGS_SEPARATOR);
            thingsInstance.name = split[0];
            thingsInstance.protocol = split[1];
            thingsInstance.server = split[2];
        }
        return thingsInstance;
    }
}
