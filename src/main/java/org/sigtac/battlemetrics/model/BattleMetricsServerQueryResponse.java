package org.sigtac.battlemetrics.model;

import java.util.List;
import java.util.Map;

public class BattleMetricsServerQueryResponse {
    public List<BattleMetricsServer> data;
    public Map<String, String> links;

    BattleMetricsServerQueryResponse(){}

    public List<BattleMetricsServer> getData() {
        return data;
    }

    public void setData(List<BattleMetricsServer> data) {
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
