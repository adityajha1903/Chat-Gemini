package com.adiandrodev.chatgemini.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class TextFragmentViewModel(private val generativeModel: GenerativeModel): ViewModel() {

    val progressDialogVisibility = MutableLiveData(false)

    val question = MutableLiveData("")

    val responseQuestion = MutableLiveData("")

    val responseAnswer = MutableLiveData("")

    fun generateContent(prompt: String, responseReceived: (response: String?) -> Unit, catchError: (message: String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text
                withContext(Dispatchers.Main) {
                    if (!responseText.isNullOrEmpty()) {
                        responseQuestion.value = prompt
                        responseAnswer.value = responseText
                    } else {
                        responseQuestion.value = ""
                        responseAnswer.value = ""
                    }
                    question.value = ""
                    responseReceived.invoke(responseText)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    question.value = ""
                    responseQuestion.value = ""
                    responseAnswer.value = ""
                    catchError.invoke(e.message)
                }
            }
        }
    }
}