package cn.huangdayu.things.common.dsl.usecase;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author huangdayu
 */
@Data
public class ThingsUseCase implements Serializable {

    private ThingsUseCaseInfo useCaseInfo;
    private Set<ThingsSubscribeInfo> subscribes;
    private Set<ThingsConsumeInfo> consumes;

}
