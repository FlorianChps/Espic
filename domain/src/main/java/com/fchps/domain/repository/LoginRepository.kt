package com.fchps.domain.repository

interface LoginRepository {
    fun storePseudo(pseudo: String): Result<String>
    fun getPseudo(): Result<String>
    fun deletePseudo(): Result<Boolean>
}