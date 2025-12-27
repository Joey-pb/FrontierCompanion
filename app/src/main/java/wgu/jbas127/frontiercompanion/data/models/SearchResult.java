package wgu.jbas127.frontiercompanion.data.models;

public class SearchResult {

    public static final String TYPE_EXHIBIT = "exhibit";
    public static final String TYPE_PANEL = "panel";
    public static final String TYPE_ARTICLE = "article";

    public String type;
    public long exhibitId;
    public String name;
    public String description;
    public String category;

    public SearchResult(String type, long exhibitId, String name,
                        String description, String category) {
        this.type = type;
        this.exhibitId = exhibitId;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExhibitId() {
        return exhibitId;
    }

    public void setExhibitId(long exhibitId) {
        this.exhibitId = exhibitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
