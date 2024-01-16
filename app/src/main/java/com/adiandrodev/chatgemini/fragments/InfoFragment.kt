package com.adiandrodev.chatgemini.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.adiandrodev.chatgemini.BuildConfig
import com.adiandrodev.chatgemini.R
import com.adiandrodev.chatgemini.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.shareAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share App")
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text) + "\n\n" + BuildConfig.PLAY_STORE_SHARE_LINK + requireActivity().packageName)
            startActivity(Intent.createChooser(intent, "Share App Via..."))
        }

        binding.rateAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PLAY_STORE_SHARE_LINK + requireActivity().packageName))
            startActivity(intent)
        }

        binding.feedbackButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.DEVELOPER_EMAIL))

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireActivity(), "Your phone doesn't have any email app", Toast.LENGTH_SHORT).show()
            }
        }

        binding.developedByButton.setOnClickListener {
            val twitterPageUrl = BuildConfig.TWITTER_BASE_URL + BuildConfig.DEVELOPER_TWITTER
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterPageUrl))
            startActivity(intent)
        }

        binding.geminiWebsiteButton.setOnClickListener {
            try {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(requireActivity(), Uri.parse(BuildConfig.GEMINI_URL))
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), "Please Check your Internet connection.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}