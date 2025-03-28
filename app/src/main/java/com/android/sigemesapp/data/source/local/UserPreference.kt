package com.android.sigemesapp.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class UserPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[FULLNAME_KEY] = user.fullname
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[GENDER] = user.gender
            preferences[PROFILE_PICTURE] = user.profile_picture
            preferences[PHONE_NUMBER] = user.phone_number
            preferences[USER_ID] = user.id
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[FULLNAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[GENDER] ?: "",
                preferences[PROFILE_PICTURE] ?: "",
                preferences[PHONE_NUMBER] ?: "",
                preferences[USER_ID] ?: -1
            )
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val EMAIL_KEY = stringPreferencesKey("userEmail")
        private val FULLNAME_KEY = stringPreferencesKey("userFullname")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val GENDER = stringPreferencesKey("gender")
        private val PROFILE_PICTURE = stringPreferencesKey("profile_picture")
        private val PHONE_NUMBER = stringPreferencesKey("phone_number")
        private val USER_ID = intPreferencesKey("1")
    }
}