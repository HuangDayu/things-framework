package cn.huangdayu.things.engine.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * @author huangdayu
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ThingsParameter {

    private Parameter parameter;
    private int index;
    private Class<?> type;
    private String name;
    private Annotation annotation;

}
