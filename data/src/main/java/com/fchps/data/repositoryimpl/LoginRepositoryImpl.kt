package com.fchps.data.repositoryimpl

import com.fchps.data.datasource.local.LoginLocalDataSource
import com.fchps.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val localDataSource: LoginLocalDataSource
) : LoginRepository {
    override fun storePseudo(pseudo: String): Result<String> {
        return localDataSource.storePseudo(pseudo)
    }

    override fun getPseudo(): Result<String> {
        return localDataSource.getPseudo()
    }

    override fun deletePseudo(): Result<Boolean> {
        return localDataSource.deletePseudo()
    }
}