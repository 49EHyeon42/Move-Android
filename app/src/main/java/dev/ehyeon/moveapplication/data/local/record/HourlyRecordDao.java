package dev.ehyeon.moveapplication.data.local.record;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HourlyRecordDao {

    @Insert
    void insertHourlyRecord(HourlyRecord hourlyRecord);

    @Query("SELECT * FROM hourlyRecord")
    LiveData<List<HourlyRecord>> selectAllHourlyRecordLiveData();
}
