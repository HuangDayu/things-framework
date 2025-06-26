package cn.huangdayu.things.ai.mcp.tools;

import cn.huangdayu.things.common.annotation.ThingsTools;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author huangdayu
 */
@ThingsTools
public class WebSearchTools {

    @Tool(description = "互联网在线搜索服务，比如搜索新闻，文档，事件等")
    public String searchWeb(@ToolParam String query) throws IOException, InterruptedException {
        String apiUrl = "https://api.duckduckgo.com/?q=" +
                URLEncoder.encode(query, StandardCharsets.UTF_8) +
                "&format=json&no_html=1";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return extractSearchResults(response.body());
    }

    private String extractSearchResults(String jsonResponse) {
        JSONObject json = JSONObject.parse(jsonResponse);
        StringBuilder results = new StringBuilder();
        if (json.containsKey("RelatedTopics")) {
            for (JSONObject relatedTopics : json.getJSONArray("RelatedTopics").toArray(JSONObject.class)) {
                if (relatedTopics.containsKey("Text")) {
                    results.append("• ").append(relatedTopics.getString("Text")).append("\n");
                }
            }
        }
        return results.toString();
    }
}