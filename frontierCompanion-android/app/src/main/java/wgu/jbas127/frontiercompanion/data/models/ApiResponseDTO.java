package wgu.jbas127.frontiercompanion.data.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponseDTO {
    @SerializedName("articles")
    @Expose
    private List<Object> articles;
    @SerializedName("narratives")
    @Expose
    private List<NarrativeDTO> narratives;
    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;

    public List<Object> getArticles() {
        return articles;
    }

    public void setArticles(List<Object> articles) {
        this.articles = articles;
    }

    public List<NarrativeDTO> getNarratives() {
        return narratives;
    }

    public void setNarratives(List<NarrativeDTO> narratives) {
        this.narratives = narratives;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

}
