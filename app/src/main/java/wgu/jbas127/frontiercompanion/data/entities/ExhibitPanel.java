package wgu.jbas127.frontiercompanion.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exhibit_panel")
public class ExhibitPanel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "exhibit_id")
    private long exhibitId;
    @ColumnInfo(name = "panel_order")
    private int panelOrder;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "image_path")
    private String imagePath;
    @ColumnInfo(name = "panel_type")
    private String panelType;

    public ExhibitPanel(long id, long exhibitId, int panelOrder, String title, String content, String imagePath, String panelType) {
        this.id = id;
        this.exhibitId = exhibitId;
        this.panelOrder = panelOrder;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.panelType = panelType;
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

    public int getPanelOrder() {
        return panelOrder;
    }

    public void setPanelOrder(int panelOrder) {
        this.panelOrder = panelOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPanelType() {
        return panelType;
    }

    public void setPanelType(String panelType) {
        this.panelType = panelType;
    }
}
