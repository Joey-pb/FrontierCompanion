package wgu.jbas127.frontiercompanion.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;

@Dao
public interface ExhibitPanelDao {
    @Query("SELECT * FROM exhibit_panel WHERE exhibit_id = :exhibitId " +
            "ORDER BY panel_order")
    LiveData<List<ExhibitPanel>> getPanelsForExhibit(long exhibitId);

    @Query("SELECT * FROM exhibit_panel WHERE exhibit_id = :exhibitId " +
            "ORDER BY panel_order")
    List<ExhibitPanel> getPanelsForExhibitSync(long exhibitId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ExhibitPanel> exhibitPanels);

    // Search
    @Query("SELECT * FROM exhibit_panel WHERE content LIKE '%' || :query || '%'")
    List<ExhibitPanel> searchExhibitPanels(String query);

}
