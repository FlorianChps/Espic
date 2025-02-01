package com.fchps.data.datasource.local.impl

import android.content.SharedPreferences
import com.fchps.data.datasource.local.LoginLocalDataSource
import javax.inject.Inject

const val SHARED_PREF_USER_PSEUDO = "SHARED_PREF_USER_PSEUDO"

class LoginLocalDataSourceImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : LoginLocalDataSource {
    override fun storePseudo(pseudo: String): Result<String> {
        return try {
            val editor = sharedPreferences.edit()
            editor.putString(
                SHARED_PREF_USER_PSEUDO,
                pseudo
            )
            editor.apply()

            Result.success(pseudo)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun getPseudo(): Result<String> {
        return try {
            val editor = sharedPreferences.getString(SHARED_PREF_USER_PSEUDO, "")
            Result.success(editor.orEmpty())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun deletePseudo(): Result<Boolean> {
        return try {
            sharedPreferences.edit().remove(SHARED_PREF_USER_PSEUDO).apply()
            Result.success(true)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}