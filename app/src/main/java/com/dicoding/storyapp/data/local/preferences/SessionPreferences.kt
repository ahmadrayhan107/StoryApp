package com.dicoding.storyapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.storyapp.data.local.model.SessionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        @Volatile
        private var INSTANCE: SessionPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private val USER_ID_KEY = stringPreferencesKey("userId")
    private val NAME_KEY = stringPreferencesKey("name")
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val IS_LOGIN = booleanPreferencesKey("isLogin")

    fun getSession(): Flow<SessionModel> {
        return dataStore.data.map { session ->
            SessionModel(
                session[NAME_KEY] ?: "",
                session[USER_ID_KEY] ?: "",
                session[TOKEN_KEY] ?: "",
                session[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun saveSession(sessionModel: SessionModel) {
        dataStore.edit { session ->
            session[NAME_KEY] = sessionModel.name
            session[USER_ID_KEY] = sessionModel.userId
            session[TOKEN_KEY] = sessionModel.token
            session[IS_LOGIN] = sessionModel.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit { session ->
            session.clear()
        }
    }
}