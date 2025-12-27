package wgu.jbas127.frontiercompanion.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "article")
public class Article {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "exhibit_id")
    private long exhibitId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "thumbnail_path")
    private String thumbnailPath;
    @ColumnInfo(name = "content_url")
    private String contentUrl;
    @ColumnInfo(name = "display_order")
    private int displayOrder;
    @ColumnInfo(name = "is_active")
    private boolean isActive;

    public Article(long id, long exhibitId, String title, String description, String thumbnailPath, String contentUrl, int displayOrder, boolean isActive) {
        this.id = id;
        this.exhibitId = exhibitId;
        this.title = title;
        this.description = description;
        this.thumbnailPath = thumbnailPath;
        this.contentUrl = contentUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExhibitId() {
        return exhibitId;
    }

    public void setExhibitId(long exhibitId) {
        this.exhibitId = exhibitId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
