package com.ifs18005.delcomtodo.di

import com.ifs18005.delcomtodo.data.pref.UserPreference
import com.ifs18005.delcomtodo.data.pref.dataStore
import com.ifs18005.delcomtodo.data.remote.retrofit.ApiConfig
import com.ifs18005.delcomtodo.data.remote.retrofit.IApiService
import com.ifs18005.delcomtodo.data.repository.AuthRepository
import com.ifs18005.delcomtodo.data.repository.LocalLostRepository
import com.ifs18005.delcomtodo.data.repository.LostRepository
import com.ifs18005.delcomtodo.data.repository.UserRepository
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

  fun provideAuthRepository(context: Context): AuthRepository {
    val pref = UserPreference.getInstance(context.dataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService: IApiService = ApiConfig.getApiService(user.token)
    return AuthRepository.getInstance(pref, apiService)
  }

  fun provideUserRepository(context: Context): UserRepository {
    val pref = UserPreference.getInstance(context.dataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService: IApiService = ApiConfig.getApiService(user.token)
    return UserRepository.getInstance(apiService)
  }

  fun provideTodoRepository(context: Context): LostRepository {
    val pref = UserPreference.getInstance(context.dataStore)
    val user = runBlocking { pref.getSession().first() }
    val apiService: IApiService = ApiConfig.getApiService(user.token)
    return LostRepository.getInstance(apiService)
  }

  fun provideLocalTodoRepository(context: Context): LocalLostRepository{
    return LocalLostRepository.getInstance(context)
  }
}