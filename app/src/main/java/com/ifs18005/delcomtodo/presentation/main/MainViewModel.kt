package com.ifs18005.delcomtodo.presentation.main

import com.ifs18005.delcomtodo.data.pref.UserModel
import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.DelcomResponse
import com.ifs18005.delcomtodo.data.remote.response.DelcomTodosResponse
import com.ifs18005.delcomtodo.data.repository.AuthRepository
import com.ifs18005.delcomtodo.data.repository.LostRepository
import com.ifs18005.delcomtodo.presentation.ViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(
  private val authRepository: AuthRepository,
  private val lostRepository: LostRepository
) : ViewModel() {

  fun getSession(): LiveData<UserModel> {
    return authRepository.getSession().asLiveData()
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
    }
  }

  fun getTodos(): LiveData<MyResult<DelcomTodosResponse>> {
    return lostRepository.getTodos(null).asLiveData()
  }

  fun putTodo(
    todoId: Int,
    title: String,
    description: String,
    isFinished: Boolean,
  ): LiveData<MyResult<DelcomResponse>> {
    return lostRepository.putTodo(
      todoId,
      title,
      description,
      isFinished,
    ).asLiveData()
  }

  companion object {
    @Volatile
    private var INSTANCE: MainViewModel? = null
    fun getInstance(
        authRepository: AuthRepository,
        lostRepository: LostRepository
    ): MainViewModel {
      synchronized(ViewModelFactory::class.java) {
        INSTANCE = MainViewModel(
          authRepository,
          lostRepository
        )
      }
      return INSTANCE as MainViewModel
    }
  }
}