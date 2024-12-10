package cn.huangdayu.things.common.wrapper;

import cn.huangdayu.things.common.enums.ThingsInstanceType;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static cn.huangdayu.things.common.constants.ThingsConstants.THINGS_SEPARATOR;

/**
 * @author huangdayu
 */
@Data
public class ThingsInstance implements Serializable {

    public ThingsInstance() {
        this.types = new LinkedHashSet<>();
    }

    /**
     * 实例标识
     */
    private String code;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 实例类型，一个实例可以是多种角色类型
     */
    private Set<ThingsInstanceType> types;


    /**
     * 有会话的
     */
    private int sessions;

    /**
     * 上游服务uri，可以是网关，也可以是边缘服务，也可以是代理服务
     */
    private String upstreamUri;

    /**
     * 本实例端点uri
     */
    private String endpointUri;


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
