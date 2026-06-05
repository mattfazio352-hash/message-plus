package com.FanMessenGer.ui.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.FanMessenGer.R
import com.FanMessenGer.databinding.ActivityChatDetailBinding
import com.FanMessenGer.ui.adapter.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private val viewModel: ChatDetailViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private var conversationId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationId = intent.getLongExtra("CONVERSATION_ID", 0)
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""
        val contactName = intent.getStringExtra("CONTACT_NAME") ?: phoneNumber

        setupUI(contactName)
        setupRecyclerView()
        observeData()
        setupMessageInput()

        viewModel.loadConversation(conversationId)
        Timber.d("ChatDetailActivity created for: $contactName")
    }

    private fun setupUI(contactName: String) {
        binding.toolbar.title = contactName
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter { message ->
            Timber.d("Message clicked: ${message.id}")
        }

        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatDetailActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                Timber.d("Messages updated: ${messages.size} items")
                messageAdapter.submitList(messages)
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun setupMessageInput() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(conversationId, messageText)
                binding.etMessage.text.clear()
                Timber.d("Message sent: $messageText")
            }
        }

        binding.btnAttach.setOnClickListener {
            Timber.d("Attachment button clicked")
            // TODO: Show attachment options
        }

        binding.btnCamera.setOnClickListener {
            Timber.d("Camera button clicked")
            // TODO: Open camera
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
