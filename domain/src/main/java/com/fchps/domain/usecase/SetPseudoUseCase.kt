package com.fchps.domain.usecase

import com.fchps.domain.repository.LoginRepository

class SetPseudoUseCase(
    private val repository: LoginRepository
) {
    operator fun invoke(pseudo: String): Result<String> = repository.storePseudo(pseudo)
}