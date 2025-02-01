package com.fchps.domain.usecase

import com.fchps.domain.repository.LoginRepository

class DeletePseudoUseCase(
    private val repository: LoginRepository
) {
    operator fun invoke(): Result<Boolean> = repository.deletePseudo()
}