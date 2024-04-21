package com.ifs18005.delcomtodo.helper

import com.ifs18005.delcomtodo.data.local.entity.DelcomTodoEntity
import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.TodosItemResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class Utils {
  companion object {
    fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
      val observerWrapper = object : Observer<T> {
        override fun onChanged(value: T) {
          observer(value)
          if (value is MyResult.Success<*> ||
            value is MyResult.Error
          ) {
            removeObserver(this)
          }
        }
      }
      observeForever(observerWrapper)
    }

    fun entitiesToResponses(entities: List<DelcomTodoEntity>):
      List<TodosItemResponse> {
      val responses = ArrayList<TodosItemResponse>()
      entities.map {
        val response = TodosItemResponse(
          cover = it.cover,
          updatedAt = it.updatedAt,
          description = it.description,
          createdAt = it.createdAt,
          id = it.id,
          title = it.title,
          isFinished = it.isFinished
        )

        responses.add(response)
      }
      return responses
    }

  }
}