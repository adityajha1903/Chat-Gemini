package com.adiandrodev.chatgemini.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import com.adiandrodev.chatgemini.R
import com.adiandrodev.chatgemini.viewmodels.TextFragmentViewModel
import com.adiandrodev.chatgemini.ViewModelFactory
import com.adiandrodev.chatgemini.databinding.FragmentTextBinding
import com.google.android.material.snackbar.Snackbar

class TextFragment : Fragment() {

    private lateinit var viewModel: TextFragmentViewModel

    private lateinit var binding: FragmentTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory())[TextFragmentViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.progress_dialog)
        viewModel.progressDialogVisibility.observe(viewLifecycleOwner) {
            setProgressDialogVisibility(it)
        }

        if (!viewModel.responseAnswer.value.isNullOrEmpty() && !viewModel.responseQuestion.value.isNullOrEmpty()) {
            binding.responseCV.visibility = View.VISIBLE
        } else {
            binding.responseCV.visibility = View.GONE
        }

        binding.questionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                generateContent()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.questionTIL.setEndIconOnClickListener {
            generateContent()
        }

        return binding.root
    }

    private fun generateContent() {
        if (!viewModel.question.value.isNullOrEmpty()) {
            viewModel.progressDialogVisibility.value = true
            viewModel.generateContent(viewModel.question.value ?: "", { response ->
                if (!response.isNullOrEmpty()) {
                    binding.responseCV.visibility = View.VISIBLE
                } else {
                    binding.responseCV.visibility = View.GONE
                    showSnackBar(getString(R.string.no_response_received))
                }
                viewModel.progressDialogVisibility.value = false
            }, { e ->
                binding.responseCV.visibility = View.GONE
                viewModel.progressDialogVisibility.value = false
                showSnackBar(e?: getString(R.string.something_went_wrong),Color.RED)
            })
        } else {
            showSnackBar(getString(R.string.empty_edit_text_message))
        }
    }

    override fun onStart() {
        if (!viewModel.responseAnswer.value.isNullOrEmpty() && !viewModel.responseQuestion.value.isNullOrEmpty()) {
            binding.responseCV.visibility = View.VISIBLE
        } else {
            binding.responseCV.visibility = View.GONE
        }
        super.onStart()
    }

    override fun onResume() {
        if (!viewModel.responseAnswer.value.isNullOrEmpty() && !viewModel.responseQuestion.value.isNullOrEmpty()) {
            binding.responseCV.visibility = View.VISIBLE
        } else {
            binding.responseCV.visibility = View.GONE
        }
        super.onResume()
    }

    private lateinit var dialog: Dialog
    private fun setProgressDialogVisibility(visibility: Boolean) {
        if (visibility && !dialog.isShowing) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } else if (!visibility && dialog.isShowing) {
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