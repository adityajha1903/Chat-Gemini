package com.adiandrodev.chatgemini

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adiandrodev.chatgemini.viewmodels.ChatFragmentViewModel
import com.adiandrodev.chatgemini.viewmodels.MultiModelFragmentViewModel
import com.adiandrodev.chatgemini.viewmodels.TextFragmentViewModel
import com.google.ai.client.generativeai.GenerativeModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(TextFragmentViewModel::class.java) -> {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = BuildConfig.API_KEY
                    )
                    TextFragmentViewModel(generativeModel)
                }
                isAssignableFrom(MultiModelFragmentViewModel::class.java) -> {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro-vision",
                        apiKey = BuildConfig.API_KEY
                    )
                    MultiModelFragmentViewModel(generativeModel)
                }
                isAssignableFrom(ChatFragmentViewModel::class.java) -> {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = BuildConfig.API_KEY
                    )
                    ChatFragmentViewModel(generativeModel)
                }
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }

}