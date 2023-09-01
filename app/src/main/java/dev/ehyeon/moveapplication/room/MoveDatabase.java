package dev.ehyeon.moveapplication.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import dev.ehyeon.moveapplication.data.local.record.HourlyRecord;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecordDao;
import dev.ehyeon.moveapplication.data.local.record.Record;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;

@Database(entities = {Record.class, HourlyRecord.class}, version = 1)
public abstract class MoveDatabase extends RoomDatabase {

    public abstract RecordDao recordDao();

    public abstract HourlyRecordDao hourlyRecordDao();
}
