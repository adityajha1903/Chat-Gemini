package com.adiandrodev.chatgemini.viewmodels

import android.content.ClipData
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.ImagePart
import com.google.ai.client.generativeai.type.TextPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

class MultiModelFragmentViewModel(private val generativeModel: GenerativeModel): ViewModel() {

    val progressDialogVisibility = MutableLiveData(false)

    val question = MutableLiveData("")

    val images = ArrayList<Image>()

    val responseQuestion = MutableLiveData("")

    val responseAnswer = MutableLiveData("")

    val responseImages = ArrayList<Image>()

    fun getAndSaveSelectedImages(
        clipData: ClipData?,
        contentResolver: ContentResolver?,
        showSnackBar: (message: String, background: Int) -> Unit,
        updateRecyclerView: (images: ArrayList<Image>) -> Unit,
        setNoImageWarningVisibility: () -> Unit
    ) {
        val count = clipData?.itemCount ?: 0
        if (images.size < 16) {
            var i = 0
            progressDialogVisibility.value = true
            viewModelScope.launch(Dispatchers.IO) {
                while (i < count && images.size < 16) {
                    val imageUri = clipData?.getItemAt(i)?.uri
                    imageUri?.let { uri ->
                        getBitmapAndAddImage(uri, contentResolver, showSnackBar, setNoImageWarningVisibility)
                    }
                    i++
                }
                if (i < count) {
                    showSnackBar.invoke(IMAGE_LIMIT_WARNING, Color.parseColor("#212533"))
                }
                withContext(Dispatchers.Main) {
                    updateRecyclerView.invoke(images)
                    progressDialogVisibility.value = false
                }
            }
        } else {
            showSnackBar.invoke(IMAGE_LIMIT_WARNING, Color.parseColor("#212533"))
        }
    }

    private suspend fun getBitmapAndAddImage(
        imageUri: Uri,
        contentResolver: ContentResolver?,
        showSnackBar: (message: String, background: Int) -> Unit,
        setNoImageWarningVisibility: () -> Unit
    ) {
        var bitmap: Bitmap?= null
        try {
            val inputStream = contentResolver?.openInputStream(imageUri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            try {
                withContext(Dispatchers.IO) {
                    inputStream?.close()
                }
            } catch (e: IOException) {
                showSnackBar(TRY_AGAIN_TEXT, Color.RED)
            }
        } catch (e: FileNotFoundException) {
            showSnackBar(TRY_AGAIN_TEXT, Color.RED)
        }
        bitmap?.let {
            val image = Image(imageUri, it)
            addImage(image)
        }
        withContext(Dispatchers.Main) {
            setNoImageWarningVisibility.invoke()
        }
    }

    fun getResponse(receiveResponse: (response: String?) -> Unit, errorReceived: (message: String?) -> Unit) {
        progressDialogVisibility.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generateContent(question.value ?: "")
                withContext(Dispatchers.Main) {
                    if (!response.isNullOrEmpty()) {
                        responseQuestion.value = question.value
                        responseAnswer.value = response
                        responseImages.clear()
                        responseImages.addAll(images)
                    } else {
                        responseQuestion.value = ""
                        responseAnswer.value = ""
                        responseImages.clear()
                    }
                    images.clear()
                    question.value = ""
                    receiveResponse.invoke(response)
                    progressDialogVisibility.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    images.clear()
                    responseImages.clear()
                    question.value = ""
                    responseQuestion.value = ""
                    responseAnswer.value = ""
                    errorReceived.invoke(e.message)
                    progressDialogVisibility.value = false
                }
            }
        }
    }

    private suspend fun generateContent(prompt: String): String?  {
        val contentBuilder = Content.Builder()
        for (image in images) {
            contentBuilder.parts.add(ImagePart(image.bitmap))
        }
        contentBuilder.parts.add(TextPart(prompt))
        val content = contentBuilder.build()
        val response = generativeModel.generateContent(content)
        return response.text?.trim()
    }

    private fun addImage(image: Image) {
        if (images.size < 16) {
            images.add(image)
        }
    }

    companion object {
        private const val IMAGE_LIMIT_WARNING = "More than 16 images aren't allowed!!"
        private const val TRY_AGAIN_TEXT = "Try again, Something went wrong!!"
    }
}

data class Image(
    val imageUri: Uri,
    val bitmap: Bitmap
)