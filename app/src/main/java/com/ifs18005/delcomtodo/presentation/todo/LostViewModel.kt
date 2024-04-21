package com.ifs18005.delcomtodo.presentation.todo

import com.ifs18005.delcomtodo.data.local.entity.DelcomTodoEntity
import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.DataAddTodoResponse
import com.ifs18005.delcomtodo.data.remote.response.DelcomResponse
import com.ifs18005.delcomtodo.data.remote.response.DelcomTodoResponse
import com.ifs18005.delcomtodo.data.repository.LocalLostRepository
import com.ifs18005.delcomtodo.data.repository.LostRepository
import com.ifs18005.delcomtodo.presentation.ViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import okhttp3.MultipartBody

class LostViewModel(
  private val lostRepository: LostRepository,
  private val localLostRepository: LocalLostRepository
) : ViewModel() {

  fun getTodo(todoId: Int): LiveData<MyResult<DelcomTodoResponse>> {
    return lostRepository.getTodo(todoId).asLiveData()
  }

  fun postTodo(
    title: String,
    description: String,
  ): LiveData<MyResult<DataAddTodoResponse>> {
    return lostRepository.postTodo(
      title,
      description
    ).asLiveData()
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

  fun deleteTodo(todoId: Int): LiveData<MyResult<DelcomResponse>> {
    return lostRepository.deleteTodo(todoId).asLiveData()
  }

  fun getLocalTodos(): LiveData<List<DelcomTodoEntity>?> {
    return localLostRepository.getAllTodos()
  }

  fun getLocalTodo(todoId: Int): LiveData<DelcomTodoEntity?> {
    return localLostRepository.get(todoId)
  }


  fun insertLocalTodo(todo: DelcomTodoEntity) {
    localLostRepository.insert(todo)
  }

  fun deleteLocalTodo(todo: DelcomTodoEntity) {
    localLostRepository.delete(todo)
  }

  fun addCoverTodo(
    todoId: Int,
    cover: MultipartBody.Part,
  ): LiveData<MyResult<DelcomResponse>> {
    return lostRepository.addCoverTodo(todoId, cover).asLiveData()
  }

  companion object {
    @Volatile
    private var INSTANCE: LostViewModel? = null
    fun getInstance(
      lostRepository: LostRepository,
      localLostRepository: LocalLostRepository,
    ): LostViewModel {
      synchronized(ViewModelFactory::class.java) {
        INSTANCE = LostViewModel(
          lostRepository,
          localLostRepository
        )
      }
      return INSTANCE as LostViewModel
    }
  }
}