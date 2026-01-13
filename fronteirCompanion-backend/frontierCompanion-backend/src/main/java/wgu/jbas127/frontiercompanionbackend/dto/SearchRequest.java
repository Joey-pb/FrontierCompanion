package wgu.jbas127.frontiercompanionbackend.dto;

public class SearchRequest {
    private String query;
    private Integer limit = 20;
    private Double threshold = 0.5;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
}
