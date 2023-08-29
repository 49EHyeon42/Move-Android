package dev.ehyeon.moveapplication.data.local.record;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert
    void insertRecord(Record record);

    @Query("SELECT * FROM record")
    LiveData<List<Record>> selectAllRecordLiveData();
}
