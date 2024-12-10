package cn.huangdayu.things.generate;


import cn.huangdayu.things.common.dsl.ThingsInfo;

/**
 * 物模型代码模板生成引擎
 *
 * @author huangdayu
 */
public interface ThingsGenerateEngine {


    void generate(ThingsInfo thingsInfo);

}
