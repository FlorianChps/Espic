package com.fchps.domain.usecase

import com.fchps.domain.repository.LoginRepository

class GetPseudoUseCase(
    private val repository: LoginRepository
) {
    operator fun invoke(): Result<String> = repository.getPseudo()
}