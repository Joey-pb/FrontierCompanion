package wgu.jbas127.frontiercompanion.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NarrativeDTO {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("exhibitId")
    @Expose
    private Integer exhibitId;
    @SerializedName("title")
    @Expose
    private Object title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("similarityScore")
    @Expose
    private Object similarityScore;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExhibitId() {
        return exhibitId;
    }

    public void setExhibitId(Integer exhibitId) {
        this.exhibitId = exhibitId;
    }

    public Object getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Object getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Object similarityScore) {
        this.similarityScore = similarityScore;
    }

}