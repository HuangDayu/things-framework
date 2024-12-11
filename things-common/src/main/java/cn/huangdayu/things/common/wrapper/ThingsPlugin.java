package cn.huangdayu.things.common.wrapper;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangdayu
 */
@Data
public class ThingsPlugin implements Serializable {

    private String name;
    private String description;
    private String version;
    private String downloadUrl;

}
