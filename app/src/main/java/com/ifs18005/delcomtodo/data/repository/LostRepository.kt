package com.ifs18005.delcomtodo.data.repository

import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.DelcomResponse
import com.ifs18005.delcomtodo.data.remote.retrofit.IApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class LostRepository private constructor(
  private val apiService: IApiService,
) {
  fun postTodo(
    title: String,
    description: String,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(
        MyResult.Success(
          apiService.postTodo(title, description).data
        )
      )
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  fun putTodo(
    todoId: Int,
    title: String,
    description: String,
    isFinished: Boolean,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(
        MyResult.Success(
          apiService.putTodo(
            todoId,
            title,
            description,
            if (isFinished) 1 else 0
          )
        )
      )
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  fun getTodos(
    isFinished: Int?,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(MyResult.Success(apiService.getTodos(isFinished)))
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  fun getTodo(
    todoId: Int,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(MyResult.Success(apiService.getTodo(todoId)))
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  fun deleteTodo(
    todoId: Int,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(MyResult.Success(apiService.deleteTodo(todoId)))
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  fun addCoverTodo(
    todoId: Int,
    cover: MultipartBody.Part,
  ) = flow {
    emit(MyResult.Loading)
    try {
      //get success message
      emit(MyResult.Success(apiService.addCoverTodo(todoId, cover)))
    } catch (e: HttpException) {
      //get error message
      val jsonInString = e.response()?.errorBody()?.string()
      emit(
        MyResult.Error(
          Gson()
            .fromJson(jsonInString, DelcomResponse::class.java)
            .message
        )
      )
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: LostRepository? = null
    fun getInstance(
      apiService: IApiService,
    ): LostRepository {
      synchronized(LostRepository::class.java) {
        INSTANCE = LostRepository(
          apiService
        )
      }
      return INSTANCE as LostRepository
    }
  }
}