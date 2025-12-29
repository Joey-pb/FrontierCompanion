package wgu.jbas127.frontiercompanion.data.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ExhibitWithContent {
    @Embedded
    public Exhibit exhibit;

    @Relation(
            parentColumn = "id",
            entityColumn = "exhibit_id"
    )
    public List<ExhibitPanel> panels;

    @Relation(
            parentColumn = "id",
            entityColumn = "exhibit_id"
    )
    public List<Article> articles;
}
