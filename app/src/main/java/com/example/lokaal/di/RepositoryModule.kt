package com.example.lokaal.di

import com.example.lokaal.data.repository.AuthRepositoryImpl
import com.example.lokaal.data.repository.MomentRepositoryImpl
import com.example.lokaal.data.repository.UserProfileRepositoryImpl
import com.example.lokaal.domain.repository.AuthRepository
import com.example.lokaal.domain.repository.MomentRepository
import com.example.lokaal.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindMomentRepository(
        impl: MomentRepositoryImpl
    ) : MomentRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ) : UserProfileRepository
}