package com.FanMessenGer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FanMessenGer.data.dao.*
import com.FanMessenGer.data.model.Conversation
import com.FanMessenGer.data.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val blockedContactDao: BlockedContactDao,
    private val spamKeywordDao: SpamKeywordDao,
    private val firewallRuleDao: FirewallRuleDao
) : ViewModel() {

    val conversations = conversationDao.getActiveConversations()
    val totalUnreadCount = conversationDao.getTotalUnreadCount()

    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            try {
                messageDao.deleteConversationMessages(conversationId)
                conversationDao.deleteConversationById(conversationId)
                Timber.d("Conversation $conversationId deleted")
            } catch (e: Exception) {
                Timber.e(e, "Error deleting conversation")
            }
        }
    }

    fun pinConversation(conversationId: Long, pinned: Boolean) {
        viewModelScope.launch {
            try {
                conversationDao.togglePin(conversationId, pinned)
                Timber.d("Conversation $conversationId pinned: $pinned")
            } catch (e: Exception) {
                Timber.e(e, "Error pinning conversation")
            }
        }
    }

    fun archiveConversation(conversationId: Long, archived: Boolean) {
        viewModelScope.launch {
            try {
                conversationDao.toggleArchive(conversationId, archived)
                Timber.d("Conversation $conversationId archived: $archived")
            } catch (e: Exception) {
                Timber.e(e, "Error archiving conversation")
            }
        }
    }
}
