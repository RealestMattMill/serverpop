package org.sigtac.battlemetrics.model;

import java.util.Map;

public class BattleMetricsServer {
    private String type;
    private String id;
    private BattleMetricsServerAttributes attributes;
    private String country;
    private String status;
    private String details;
    private Map<String, Object> relationships;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BattleMetricsServerAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(BattleMetricsServerAttributes attributes) {
        this.attributes = attributes;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        return "BattleMetricsServer{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", attributes=" + attributes +
                ", country='" + country + '\'' +
                ", status='" + status + '\'' +
                ", details='" + details + '\'' +
                ", relationships=" + relationships +
                '}';
    }
}
