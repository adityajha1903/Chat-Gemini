package com.adiandrodev.chatgemini.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragmentViewModel(private val generativeModel: GenerativeModel): ViewModel() {

    val progressDialogVisibility = MutableLiveData(false)

    val editTextPrompt = MutableLiveData("")

    val chats = ArrayList<Chat>()

    var staredChat: com.google.ai.client.generativeai.Chat? = null

    fun generateChat(prompt: String, newChatAdded: (position: Int) -> Unit) {
        progressDialogVisibility.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if (staredChat == null) {
                staredChat = generativeModel.startChat()
            }
            try {
                val content = staredChat?.sendMessage(prompt)
                withContext(Dispatchers.Main) {
                    if (content?.text.isNullOrEmpty()) {
                        chats.add(Chat(Chat.ROLE_MODEL, "", Chat.NO_RESPONSE_RECEIVED_ERROR))
                    } else {
                        chats.add(Chat(Chat.ROLE_MODEL, content?.text ?: "", ""))
                    }
                    newChatAdded.invoke(chats.size - 1)
                    progressDialogVisibility.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    chats.add(Chat(Chat.ROLE_MODEL, "", e.message ?: Chat.NO_RESPONSE_RECEIVED_ERROR))
                    newChatAdded.invoke(chats.size - 1)
                    progressDialogVisibility.value = false
                }
            }
        }
    }
}

data class Chat(
    val role: String,
    val prompt: String,
    val errorMessage: String
) {
    companion object {
        const val ROLE_MODEL = "Model"
        const val ROLE_YOU = "You"
        const val NO_RESPONSE_RECEIVED_ERROR = "No response received!!"
    }
}