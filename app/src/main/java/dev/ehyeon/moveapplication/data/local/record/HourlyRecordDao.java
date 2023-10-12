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

    @Query("SELECT * FROM HourlyRecord")
    LiveData<List<HourlyRecord>> selectAllHourlyRecordLiveData();

    @Query("UPDATE HourlyRecord SET " +
            "totalTravelDistance = totalTravelDistance + :totalTravelDistance, " +
            "averageSpeed = averageSpeed + :averageSpeed, " +
            "step = step + :step, " +
            "calorieConsumption = calorieConsumption + :calorieConsumption WHERE id = :id")
    void updateHourlyRecordById(long id, float totalTravelDistance, float averageSpeed, int step, float calorieConsumption);

    @Query("SELECT EXISTS(SELECT * FROM HourlyRecord WHERE id = :id)")
    boolean existsHourlyRecordById(long id);
}
