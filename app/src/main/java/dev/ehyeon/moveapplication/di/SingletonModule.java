package dev.ehyeon.moveapplication.di;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecordDao;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.room.MoveDatabase;

@Module
@InstallIn(SingletonComponent.class)
public class SingletonModule {

    @Provides
    @Singleton
    public MoveDatabase provideMoveDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, MoveDatabase.class, "Move Database").build();
    }

    @Provides
    @Singleton
    public RecordDao provideRecordDao(MoveDatabase moveDatabase) {
        return moveDatabase.recordDao();
    }

    @Provides
    @Singleton
    public HourlyRecordDao provideHourlyRecordDao(MoveDatabase moveDatabase) {
        return moveDatabase.hourlyRecordDao();
    }
}
