package cn.huangdayu.things.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议结构： [帧头(2B)][协议版本(1B)][分段id(2B)][消息id(4B)][序列化方式(1B)][加密方式(1B)][消息负载数(1B(N))]
 * [[消息字段类别(1B)][消息字段值类型(1B)][消息字段值长度(2B(M))][消息字段值(M*B)]*N][加密校验位(2B)][帧尾(2B)]
 * @author huangdayu
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class HexThingsMessage extends JsonThingsMessage {

    public HexThingsMessage(byte[] thingsMessageBytes) {

    }

    public HexThingsMessage(String thingsMessageHex) {

    }

    public String hexMessage() {
        return null;
    }


    public byte[] bytesMessage() {
        return null;
    }

}
