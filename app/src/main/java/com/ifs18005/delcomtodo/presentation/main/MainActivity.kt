package com.ifs18005.delcomtodo.presentation.main

import com.ifs18005.delcomtodo.R
import com.ifs18005.delcomtodo.adapter.LostAdapter
import com.ifs18005.delcomtodo.data.remote.MyResult
import com.ifs18005.delcomtodo.data.remote.response.DelcomTodosResponse
import com.ifs18005.delcomtodo.data.remote.response.TodosItemResponse
import com.ifs18005.delcomtodo.databinding.ActivityMainBinding
import com.ifs18005.delcomtodo.helper.Utils.Companion.observeOnce
import com.ifs18005.delcomtodo.presentation.ViewModelFactory
import com.ifs18005.delcomtodo.presentation.login.LoginActivity
import com.ifs18005.delcomtodo.presentation.profile.ProfileActivity
import com.ifs18005.delcomtodo.presentation.todo.LostDetailActivity
import com.ifs18005.delcomtodo.presentation.todo.LostFavoriteActivity
import com.ifs18005.delcomtodo.presentation.todo.LostManageActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private val viewModel by viewModels<MainViewModel> {
    ViewModelFactory.getInstance(this)
  }

  private val launcher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == LostManageActivity.RESULT_CODE) {
      recreate()
    }

    if (result.resultCode == LostDetailActivity.RESULT_CODE) {
      result.data?.let {
        val isChanged = it.getBooleanExtra(
          LostDetailActivity.KEY_IS_CHANGED,
          false
        )

        if (isChanged) {
          recreate()
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupView()
    setupAction()
  }

  private fun setupView() {
    showComponentNotEmpty(false)
    showEmptyError(false)
    showLoading(true)

    binding.appbarMain.overflowIcon =
      ContextCompat
        .getDrawable(this, R.drawable.ic_more_vert_24)

    observeGetTodos()
  }

  private fun setupAction() {
    binding.appbarMain.setOnMenuItemClickListener { menuItem ->
      when (menuItem.itemId) {
        R.id.mainMenuProfile -> {
          openProfileActivity()
          true
        }

        R.id.mainMenuFavoriteTodos -> {
          openFavoriteTodoActivity()
          true
        }


        R.id.mainMenuLogout -> {
          viewModel.logout()
          openLoginActivity()
          true
        }

        else -> false
      }
    }

    binding.fabMainAddTodo.setOnClickListener {
      openAddTodoActivity()
    }

    viewModel.getSession().observe(this) { user ->
      if (!user.isLogin) {
        openLoginActivity()
      } else {
        observeGetTodos()
      }
    }
  }

  private fun observeGetTodos() {
    viewModel.getTodos().observe(this) { result ->
      if (result != null) {
        when (result) {
          is MyResult.Loading -> {
            showLoading(true)
          }

          is MyResult.Success -> {
            showLoading(false)
            loadTodosToLayout(result.data)
          }

          is MyResult.Error -> {
            showLoading(false)
            showEmptyError(true)
          }
        }
      }
    }
  }

  private fun loadTodosToLayout(response: DelcomTodosResponse) {
    val todos = response.data.todos
    val layoutManager = LinearLayoutManager(this)
    binding.rvMainTodos.layoutManager = layoutManager
    val itemDecoration = DividerItemDecoration(
      this,
      layoutManager.orientation
    )
    binding.rvMainTodos.addItemDecoration(itemDecoration)

    if (todos.isEmpty()) {
      showEmptyError(true)
      binding.rvMainTodos.adapter = null
    } else {
      showComponentNotEmpty(true)
      showEmptyError(false)

      val adapter = LostAdapter()
      adapter.submitOriginalList(todos)
      binding.rvMainTodos.adapter = adapter
      adapter.setOnItemClickCallback(object : LostAdapter.OnItemClickCallback {
        override fun onCheckedChangeListener(
          todo: TodosItemResponse,
          isChecked: Boolean
        ) {
          adapter.filter(binding.svMain.query.toString())

          viewModel.putTodo(
            todo.id,
            todo.title,
            todo.description,
            isChecked
          ).observeOnce {
            when (it) {
              is MyResult.Error -> {
                if (isChecked) {
                  Toast.makeText(
                    this@MainActivity,
                    "Gagal menyelesaikan todo: " + todo.title,
                    Toast.LENGTH_SHORT
                  ).show()
                } else {
                  Toast.makeText(
                    this@MainActivity,
                    "Gagal batal menyelesaikan todo: " + todo.title,
                    Toast.LENGTH_SHORT
                  ).show()
                }
              }

              is MyResult.Success -> {
                if (isChecked) {
                  Toast.makeText(
                    this@MainActivity,
                    "Berhasil menyelesaikan todo: " + todo.title,
                    Toast.LENGTH_SHORT
                  ).show()
                } else {
                  Toast.makeText(
                    this@MainActivity,
                    "Berhasil batal menyelesaikan todo: " + todo.title,
                    Toast.LENGTH_SHORT
                  ).show()
                }
              }

              else -> {}
            }
          }
        }

        override fun onClickDetailListener(todoId: Int) {
          val intent = Intent(
            this@MainActivity,
            LostDetailActivity::class.java
          )
          intent.putExtra(LostDetailActivity.KEY_TODO_ID, todoId)
          launcher.launch(intent)
        }
      })

      binding.svMain.setOnQueryTextListener(
        object : SearchView.OnQueryTextListener {
          override fun onQueryTextSubmit(query: String): Boolean {
            return false
          }

          override fun onQueryTextChange(newText: String): Boolean {
            adapter.filter(newText)
            binding.rvMainTodos.layoutManager?.scrollToPosition(0)
            return true
          }
        })
    }
  }

  private fun showLoading(isLoading: Boolean) {
    binding.pbMain.visibility =
      if (isLoading) View.VISIBLE else View.GONE
  }

  private fun openProfileActivity() {
    val intent = Intent(applicationContext, ProfileActivity::class.java)
    startActivity(intent)
  }

  private fun showComponentNotEmpty(status: Boolean) {
    binding.svMain.visibility =
      if (status) View.VISIBLE else View.GONE

    binding.rvMainTodos.visibility =
      if (status) View.VISIBLE else View.GONE
  }

  private fun showEmptyError(isError: Boolean) {
    binding.tvMainEmptyError.visibility =
      if (isError) View.VISIBLE else View.GONE
  }

  private fun openLoginActivity() {
    val intent = Intent(applicationContext, LoginActivity::class.java)
    intent.flags =
      Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    finish()
  }

  private fun openAddTodoActivity() {
    val intent = Intent(
      this@MainActivity,
      LostManageActivity::class.java
    )
    intent.putExtra(LostManageActivity.KEY_IS_ADD, true)
    launcher.launch(intent)
  }

  private fun openFavoriteTodoActivity() {
    val intent = Intent(
      this@MainActivity,
      LostFavoriteActivity::class.java
    )
    launcher.launch(intent)
  }
}