package com.FanMessenGer.ui.newmessage

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.FanMessenGer.databinding.ActivityNewMessageBinding
import com.FanMessenGer.ui.adapter.ContactAdapter
import com.FanMessenGer.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding
    private val viewModel: NewMessageViewModel by viewModels()
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        observeData()
        checkPermissions()

        Timber.d("NewMessageActivity created")
    }

    private fun setupUI() {
        binding.toolbar.title = "New Message"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.etPhoneNumber.hint = "Enter phone number or name"
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter { contact ->
            Timber.d("Contact selected: ${contact.displayName}")
            // TODO: Start new conversation
        }

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(this@NewMessageActivity)
            adapter = contactAdapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                Timber.d("Contacts updated: ${contacts.size} items")
                contactAdapter.submitList(contacts)
            }
        }
    }

    private fun checkPermissions() {
        if (!PermissionUtil.hasContactsPermission(this)) {
            Timber.w("Contacts permission not granted")
            // TODO: Request permission
        }
    }
}
