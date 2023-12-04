package hu.kts.wtracker.di

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.kts.wtracker.R
import hu.kts.wtracker.persistency.SessionDao
import hu.kts.wtracker.persistency.WTrackerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideClock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Provides
    fun provideTextToSpeech(@ApplicationContext context: Context): TextToSpeech {
        return TextToSpeech(context) { status ->
            if (status == TextToSpeech.ERROR) {
                Toast.makeText(context, R.string.text_to_speech_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WTrackerDatabase {
        return Room.databaseBuilder(
            context,
            WTrackerDatabase::class.java, "wtrackerdb"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: WTrackerDatabase): SessionDao {
        return database.sessionDao()
    }

}
