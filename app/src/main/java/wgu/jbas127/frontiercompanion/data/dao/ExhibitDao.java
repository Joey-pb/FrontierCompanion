package wgu.jbas127.frontiercompanion.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.Exhibit;

@Dao
public interface ExhibitDao {
    @Query("SELECT * FROM exhibit")
    LiveData<List<Exhibit>> getAllExhibits();

    @Query("SELECT * FROM exhibit")
    List<Exhibit> getAllExhibitsSync();


    @Query("SELECT * FROM exhibit WHERE id = :id")
    LiveData<Exhibit> getExhibitById(long id);

    @Query("SELECT * FROM exhibit WHERE id = :id")
    Exhibit getExhibitByIdSync(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Exhibit> exhibits);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Exhibit exhibit);

    @Update
    int update(Exhibit exhibit);

    @Delete
    int delete(Exhibit exhibit);

    @Query("SELECT * FROM exhibit WHERE " +
            "name LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' OR " +
            "location LIKE '%' || :query || '%' OR " +
            "era LIKE '%' || :query || '%'")
    List<Exhibit> searchExhibits(String query);
}
