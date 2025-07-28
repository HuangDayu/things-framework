package cn.huangdayu.things.sofaark.multiple;

import cn.huangdayu.things.api.sofabus.ThingsSofaBusDescriber;
import cn.huangdayu.things.common.annotation.ThingsBean;
import cn.huangdayu.things.common.dsl.ThingsDslInfo;
import cn.huangdayu.things.sofaark.condition.ThingsSofaArkMultipleCondition;
import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import org.springframework.context.annotation.Conditional;

import static cn.huangdayu.things.sofaark.utils.ThingsSofaArkUtils.getBizService;


/**
 * @author huangdayu
 */
@Conditional(ThingsSofaArkMultipleCondition.class)
@ThingsBean
public class ThingsSofaArkDescriber implements ThingsSofaBusDescriber {


    @Override
    public ThingsDslInfo getDSL() {
        ThingsDslInfo thingsDslInfo = new ThingsDslInfo();
        Biz masterBiz = ArkClient.getMasterBiz();
        ArkClient.getBizManagerService().getBizInOrder().forEach(biz -> {
            if (biz != null && biz.getBizState().equals(BizState.ACTIVATED) && !biz.equals(masterBiz)) {
                ThingsSofaBusDescriber thingsSofaBusDescriber = getBizService(biz.getBizName(), biz.getBizVersion(), ThingsSofaBusDescriber.class);
                if (thingsSofaBusDescriber != null) {
                    ThingsDslInfo dslInfo = thingsSofaBusDescriber.getDSL();
                    thingsDslInfo.getUseCaseDsl().addAll(dslInfo.getUseCaseDsl());
                    thingsDslInfo.getThingsDsl().addAll(dslInfo.getThingsDsl());
                }
            }
        });
        return thingsDslInfo;
    }
}
