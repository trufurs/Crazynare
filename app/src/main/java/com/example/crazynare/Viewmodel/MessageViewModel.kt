package com.example.crazynare.Viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<MutableList<String>>(mutableListOf())
    val messages: LiveData<MutableList<String>> get() = _messages

    private val _question = MutableLiveData<String>("Chat Box")
    val question: LiveData<String> get() = _question

    fun addQuestion(Question:String){
        question.value.equals(Question)
        _question.postValue(_question.value)
    }
    fun addMessage(message: String) {
        messages.value?.add(message)
        _messages.postValue(_messages.value)
        Log.d("MessageViewModel", "Added message: $message, Current messages: ${_messages.value}")
    }

    fun updateMessages(updatedMessages: MutableList<String>) {
        _messages.value = updatedMessages
    }

    fun clearMessages() {
        _messages.value = mutableListOf()
    }

    fun removeMessage(message: String) {
        _messages.value?.remove(message)

    }
}
