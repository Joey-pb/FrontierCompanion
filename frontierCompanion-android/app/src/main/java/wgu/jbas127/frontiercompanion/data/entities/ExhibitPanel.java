package wgu.jbas127.frontiercompanion.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "exhibit_panel",
        foreignKeys = @ForeignKey(entity = Exhibit.class,
        parentColumns = "id",
        childColumns = "exhibit_id",
        onDelete = ForeignKey.CASCADE))
public class ExhibitPanel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "exhibit_id")
    private long exhibitId;
    @ColumnInfo(name = "panel_order")
    private int panelOrder;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "content_position")
    private String contentPosition;
    @ColumnInfo(name = "image_res_name")
    private String imageResName;

    public ExhibitPanel(long exhibitId, int panelOrder,
                         String content, String contentPosition, String imageResName) {
        this.exhibitId = exhibitId;
        this.panelOrder = panelOrder;
        this.content = content;
        this.contentPosition = contentPosition;
        this.imageResName = imageResName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentPosition() {
        return contentPosition;
    }

    public void setContentPosition(String contentPosition) {
        this.contentPosition = contentPosition;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExhibitPanel that = (ExhibitPanel) o;
        return id == that.id && exhibitId == that.exhibitId && panelOrder == that.panelOrder && Objects.equals(content, that.content) && Objects.equals(contentPosition, that.contentPosition) && Objects.equals(imageResName, that.imageResName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exhibitId, panelOrder, content, contentPosition, imageResName);
    }
}
