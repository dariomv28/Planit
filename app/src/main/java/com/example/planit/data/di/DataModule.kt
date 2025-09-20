package com.example.planit.data.di

import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.planit.data.repository.CalendarRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideCalendarRepository(
        db: FirebaseFirestore
    ): CalendarRepository = CalendarRepository(db)
}