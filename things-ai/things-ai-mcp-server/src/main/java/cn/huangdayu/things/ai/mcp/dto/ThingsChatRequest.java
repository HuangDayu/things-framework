package cn.huangdayu.things.ai.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangdayu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThingsChatRequest {
    private String chatId;
    private String model;
    private String prompt;
    private boolean onlineSearch;
    private boolean deepThink;
    private boolean enabledTools;
}
