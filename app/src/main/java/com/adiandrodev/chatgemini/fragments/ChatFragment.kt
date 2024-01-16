package com.adiandrodev.chatgemini.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adiandrodev.chatgemini.viewmodels.Chat
import com.adiandrodev.chatgemini.viewmodels.ChatFragmentViewModel
import com.adiandrodev.chatgemini.adapters.ChatRecyclerAdapter
import com.adiandrodev.chatgemini.R
import com.adiandrodev.chatgemini.ViewModelFactory
import com.adiandrodev.chatgemini.databinding.ClearChatPermissionDialogBinding
import com.adiandrodev.chatgemini.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var viewModel: ChatFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory())[ChatFragmentViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.progress_dialog)
        viewModel.progressDialogVisibility.observe(viewLifecycleOwner) {
            setProgressDialogVisibility(it)
        }

        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatsRecyclerView.adapter = ChatRecyclerAdapter(viewModel.chats, requireActivity().resources.displayMetrics)
        binding.chatsRecyclerView.scrollToPosition(viewModel.chats.size - 1)

        binding.clearChat.setOnClickListener {
            if (viewModel.staredChat != null) {
                showClearChatPermissionDialog()
            }
        }

        binding.messageEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                hideKeyboard()
                generateContent()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.sendMessageBtn.setOnClickListener {
            hideKeyboard()
            generateContent()
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.messageEditText.windowToken, 0)
    }

    private fun generateContent() {
        if (!viewModel.editTextPrompt.value.isNullOrEmpty()) {
            val prompt = viewModel.editTextPrompt.value
            viewModel.editTextPrompt.value = ""
            viewModel.chats.add(Chat(Chat.ROLE_YOU, prompt?:"", ""))
            binding.chatsRecyclerView.adapter?.notifyItemInserted(viewModel.chats.size - 1)
            binding.chatsRecyclerView.scrollToPosition(viewModel.chats.size - 1)
            viewModel.generateChat(prompt ?: "") {
                binding.chatsRecyclerView.adapter?.notifyItemInserted(it)
                binding.chatsRecyclerView.scrollToPosition(viewModel.chats.size - 1)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showClearChatPermissionDialog() {
        val permissionDialog = Dialog(requireActivity())
        val dialogBinding = ClearChatPermissionDialogBinding.inflate(layoutInflater)
        permissionDialog.setContentView(dialogBinding.root)
        dialogBinding.noBtn.setOnClickListener {
             permissionDialog.dismiss()
        }
        dialogBinding.yesBtn.setOnClickListener {
            viewModel.chats.clear()
            binding.chatsRecyclerView.adapter?.notifyDataSetChanged()
            viewModel.progressDialogVisibility.value = false
            viewModel.editTextPrompt.value = ""
            viewModel.staredChat = null
            permissionDialog.dismiss()
        }
        permissionDialog.show()
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
}