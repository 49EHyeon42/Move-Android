package dev.ehyeon.moveapplication.di;

import android.content.Context;

import androidx.room.Room;

import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dev.ehyeon.moveapplication.BuildConfig;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecordDao;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.data.remote.movestop.MoveStopService;
import dev.ehyeon.moveapplication.data.remote.sign.SignService;
import dev.ehyeon.moveapplication.room.MoveDatabase;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
    }

    @Provides
    @Singleton
    public SignService provideSignService(Retrofit retrofit) {
        return retrofit.create(SignService.class);
    }

    @Provides
    @Singleton
    public MoveStopService provideMoveStopService(Retrofit retrofit) {
        return retrofit.create(MoveStopService.class);
    }
}
