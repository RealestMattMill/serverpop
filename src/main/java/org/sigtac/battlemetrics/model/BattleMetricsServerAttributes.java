package org.sigtac.battlemetrics.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BattleMetricsServerAttributes {
    private String id;
    private String name;
    private String ip;
    private Integer port;
    private Integer portQuery;
    private Integer players;
    private Integer maxPlayers;
    private Integer rank;
    private String createdAt;
    private String updatedAt;
    private List<Integer> location;
    private String country;
    private String status;
    private Map<String, Object> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPortQuery() {
        return portQuery;
    }

    public void setPortQuery(Integer portQuery) {
        this.portQuery = portQuery;
    }

    public Integer getPlayers() {
        return players;
    }

    public void setPlayers(Integer players) {
        this.players = players;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Integer> getLocation() {
        return location;
    }

    public void setLocation(List<Integer> location) {
        this.location = location;
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

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "BattleMetricsServerAttributes{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", portQuery=" + portQuery +
                ", players=" + players +
                ", maxPlayers=" + maxPlayers +
                ", rank=" + rank +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", location=" + location +
                ", country='" + country + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
