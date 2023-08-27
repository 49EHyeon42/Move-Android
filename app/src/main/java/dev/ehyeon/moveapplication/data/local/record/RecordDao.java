package dev.ehyeon.moveapplication.data.local.record;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface RecordDao {

    @Insert
    void insertRecord(Record record);
}
