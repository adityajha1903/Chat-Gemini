package com.adiandrodev.chatgemini.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adiandrodev.chatgemini.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private lateinit var imageUri: Uri
    private var imageIndex = 0
    private var showDeleteBtn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = Uri.parse(it.getString(IMAGE_URI_KEY))
            imageIndex = it.getInt(IMAGE_INDEX_KEY)
            showDeleteBtn = it.getBoolean(SHOW_DELETE_BTN_KEY)
        }
    }

    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        binding.imageView.setImageURI(imageUri)

        if (showDeleteBtn) {
            binding.dltImgBtn.visibility = View.VISIBLE
        } else {
            binding.dltImgBtn.visibility = View.GONE
        }

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.doneBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.dltImgBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(DELETE_IMAGE_INDEX_KEY, imageIndex)
            }
            parentFragmentManager.setFragmentResult(DELETE_IMAGE_REQUEST_KEY, bundle)
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    companion object {
        private const val IMAGE_URI_KEY = "image_uri_key"
        private const val IMAGE_INDEX_KEY = "image_index_key"
        const val DELETE_IMAGE_REQUEST_KEY = "delete_image_request_key"
        const val DELETE_IMAGE_INDEX_KEY = "delete_image_index_key"
        private const val SHOW_DELETE_BTN_KEY = "show_delete_btn_key"
        const val FRAGMENT_IMAGE_TRANSITION_NAME = "fragment_image_transition_name"

        @JvmStatic
        fun newInstance(imageUri: String, imageIndex: Int, showDeleteBtn: Boolean) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_URI_KEY, imageUri)
                    putInt(IMAGE_INDEX_KEY, imageIndex)
                    putBoolean(SHOW_DELETE_BTN_KEY, showDeleteBtn)
                }
            }
    }
}