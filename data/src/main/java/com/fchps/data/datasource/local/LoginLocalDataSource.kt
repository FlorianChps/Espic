package com.fchps.data.datasource.local

interface LoginLocalDataSource {
    fun storePseudo(pseudo: String): Result<String>
    fun getPseudo(): Result<String>
    fun deletePseudo(): Result<Boolean>
}