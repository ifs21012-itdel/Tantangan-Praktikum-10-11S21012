package com.ifs18005.delcomtodo.presentation.register

import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.DelcomResponse
import com.ifs18005.delcomtodo.data.repository.AuthRepository
import com.ifs18005.delcomtodo.presentation.ViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class RegisterViewModel(
  private val authRepository: AuthRepository,
) : ViewModel() {

  fun register(name: String, email: String, password: String):
    LiveData<MyResult<DelcomResponse>> {
    return authRepository.register(name, email, password).asLiveData()
  }

  companion object {
    @Volatile
    private var INSTANCE: RegisterViewModel? = null
    fun getInstance(
      authRepository: AuthRepository
    ): RegisterViewModel {
      synchronized(ViewModelFactory::class.java) {
        INSTANCE = RegisterViewModel(
          authRepository
        )
      }
      return INSTANCE as RegisterViewModel
    }
  }
}