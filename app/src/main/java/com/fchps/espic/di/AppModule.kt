package com.fchps.espic.di

import com.fchps.domain.repository.CameraRepository
import com.fchps.domain.repository.FeedRepository
import com.fchps.domain.repository.LoginRepository
import com.fchps.domain.usecase.AddFeedUseCase
import com.fchps.domain.usecase.DeletePseudoUseCase
import com.fchps.domain.usecase.GetFeedUseCase
import com.fchps.domain.usecase.GetPseudoUseCase
import com.fchps.domain.usecase.SetPseudoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    fun provideGetFeedUseCase(repository: FeedRepository): GetFeedUseCase =
        GetFeedUseCase(repository)

    @Provides
    fun provideAddFeedUseCase(repository: FeedRepository): AddFeedUseCase =
        AddFeedUseCase(repository)

    @Provides
    fun provideSetPseudoUseCase(repository: LoginRepository): SetPseudoUseCase =
        SetPseudoUseCase(repository)

    @Provides
    fun provideGetPseudoUseCase(repository: LoginRepository): GetPseudoUseCase =
        GetPseudoUseCase(repository)

    @Provides
    fun provideDeletePseudoUseCase(repository: LoginRepository): DeletePseudoUseCase =
        DeletePseudoUseCase(repository)
}
