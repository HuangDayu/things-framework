package cn.huangdayu.things.common.wrapper;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;

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
     * 有会话的
     */
    private int sessions;

    /**
     * 是否使用代理
     */
    private boolean useBroker;

    /**
     * 上游服务端点信息，没有则为空
     */
    private String brokerUri;

    /**
     * 本实例端点信息
     */
    private String endpointUri;


    /**
     * 提供的物模型的productCode
     */
    private Set<String> provides = new LinkedHashSet<>();


    /**
     * 消费的物模型的productCode
     * 消费的服务，订阅的事件
     */
    private Set<String> consumes = new LinkedHashSet<>();


    /**
     * 订阅列表
     */
    private Set<String> subscribes = new LinkedHashSet<>();


    public String getCode() {
        return name + THINGS_SEPARATOR + endpointUri;
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
            thingsInstance.endpointUri = split[1];
        }
        return thingsInstance;
    }
}
