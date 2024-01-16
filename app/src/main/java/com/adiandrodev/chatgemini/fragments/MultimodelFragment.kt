package com.adiandrodev.chatgemini.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adiandrodev.chatgemini.viewmodels.MultiModelFragmentViewModel
import com.adiandrodev.chatgemini.R
import com.adiandrodev.chatgemini.adapters.RecyclerImageAdapter
import com.adiandrodev.chatgemini.ViewModelFactory
import com.adiandrodev.chatgemini.databinding.FragmentMultimodelBinding
import com.google.android.material.snackbar.Snackbar

class MultimodelFragment : Fragment() {

    private lateinit var binding: FragmentMultimodelBinding

    private lateinit var viewModel: MultiModelFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultimodelBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory())[MultiModelFragmentViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.progress_dialog)
        viewModel.progressDialogVisibility.observe(viewLifecycleOwner) {
            setProgressDialogVisibility(it)
        }

        setNoImgWarning()

        binding.imagesRecyclerView.adapter = RecyclerImageAdapter(viewModel.images) { imageUri, imageIndex ->
            loadImageFragment(imageUri, imageIndex, true)
        }
        binding.imagesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.imagesRecyclerViewAnswer.adapter = RecyclerImageAdapter(viewModel.responseImages) { imageUri, imageIndex ->
            loadImageFragment(imageUri, imageIndex, false)
        }
        binding.imagesRecyclerViewAnswer.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        if (!viewModel.responseAnswer.value.isNullOrEmpty() && !viewModel.responseQuestion.value.isNullOrEmpty() && viewModel.responseImages.size != 0) {
            binding.responseCV.visibility = View.VISIBLE
        } else {
            binding.responseCV.visibility = View.GONE
        }

        binding.addImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResultLauncher.launch(intent)
        }

        binding.questionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getResponse()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.questionTIL.setEndIconOnClickListener {
            getResponse()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(ImageFragment.DELETE_IMAGE_REQUEST_KEY, this) { _, bundle ->
            val index = bundle.getInt(ImageFragment.DELETE_IMAGE_INDEX_KEY)
            index.let {
                viewModel.images.removeAt(it)
                (binding.imagesRecyclerView.adapter as RecyclerImageAdapter).dataUpdated(viewModel.images)
            }
        }

        return binding.root
    }

    private fun loadImageFragment(imageUri: Uri, imageIndex: Int, showDeleteBtn: Boolean) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            .add(R.id.fragmentContainerView, ImageFragment.newInstance(imageUri.toString(), imageIndex, showDeleteBtn))
            .addToBackStack(null)
            .commit()
    }

    private fun setNoImgWarning() {
        if (viewModel.images.size == 0) {
            binding.noImgAddedWarningTv.visibility = View.VISIBLE
        } else {
            binding.noImgAddedWarningTv.visibility = View.GONE
        }
    }

    private fun getResponse() {
        if (!viewModel.question.value.isNullOrEmpty() && viewModel.images.isNotEmpty()) {
            viewModel.getResponse({
                setNoImgWarning()
                (binding.imagesRecyclerView.adapter as RecyclerImageAdapter).dataUpdated(viewModel.images)
                (binding.imagesRecyclerViewAnswer.adapter as RecyclerImageAdapter).dataUpdated(viewModel.responseImages)
                if (!it.isNullOrEmpty()) {
                    binding.responseCV.visibility = View.VISIBLE
                } else {
                    binding.responseCV.visibility = View.GONE
                    showSnackBar(getString(R.string.no_response_received))
                }
            }, {
                setNoImgWarning()
                (binding.imagesRecyclerView.adapter as RecyclerImageAdapter).dataUpdated(viewModel.images)
                (binding.imagesRecyclerViewAnswer.adapter as RecyclerImageAdapter).dataUpdated(viewModel.responseImages)
                binding.responseCV.visibility = View.GONE
                showSnackBar(it?: getString(R.string.something_went_wrong), Color.RED)
            })
        } else {
            showSnackBar(getString(R.string.empty_edit_text_message))
        }
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data?.clipData != null) {
                viewModel.getAndSaveSelectedImages(result.data?.clipData, context?.contentResolver, { message, background ->
                    showSnackBar(message, background)
                }, {
                    (binding.imagesRecyclerView.adapter as RecyclerImageAdapter).dataUpdated(it)
                }, {
                    setNoImgWarning()
                })
            }
    }

    private lateinit var dialog: Dialog
    private fun setProgressDialogVisibility(visible: Boolean) {
        if (visible && !dialog.isShowing) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } else if (!visible && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun showSnackBar(message: String = getString(R.string.something_went_wrong), background: Int = Color.parseColor("#212533")) {
        val snackBar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackBar.setTextColor(Color.WHITE)
        snackBar.setBackgroundTint(background)
        snackBar.show()
    }
}