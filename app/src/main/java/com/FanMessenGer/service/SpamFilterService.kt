package com.FanMessenGer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.FanMessenGer.data.MessageDatabase
import com.FanMessenGer.data.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SpamFilterService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("SpamFilterService started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SpamFilterService destroyed")
    }

    companion object {
        fun checkMessageForSpam(context: Context, message: Message, db: MessageDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var isSpam = false
                    var spamReason = ""

                    // Check against spam keywords
                    val keywords = db.spamKeywordDao().getActiveKeywords()
                    keywords.forEach { keyword ->
                        if (message.body.contains(keyword.keyword, ignoreCase = true)) {
                            isSpam = true
                            spamReason = "Contains spam keyword: ${keyword.keyword}"
                            return@forEach
                        }
                    }

                    // Check against firewall rules
                    if (!isSpam) {
                        val phoneRules = db.firewallRuleDao().getRulesByPhoneNumber(message.phoneNumber)
                        phoneRules.forEach { rule ->
                            if (rule.action == "BLOCK") {
                                isSpam = true
                                spamReason = "Blocked by firewall: ${rule.ruleType}"
                                return@forEach
                            }
                        }
                    }

                    // Check if sender is blocked
                    if (!isSpam) {
                        val isBlocked = db.blockedContactDao().isContactBlocked(message.phoneNumber)
                        if (isBlocked) {
                            isSpam = true
                            spamReason = "Sender is blocked"
                        }
                    }

                    // Update message with spam status
                    if (isSpam) {
                        val updatedMessage = message.copy(
                            isSpam = true,
                            spamReason = spamReason,
                            isBlocked = true
                        )
                        db.messageDao().updateMessage(updatedMessage)
                        Timber.w("Message marked as spam: $spamReason")
                    }

                } catch (e: Exception) {
                    Timber.e(e, "Error checking message for spam")
                }
            }
        }

        fun initializeDefaultSpamKeywords(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = MessageDatabase.getInstance(context)
                
                // Default spam keywords
                val defaultKeywords = listOf(
                    "click here",
                    "verify account",
                    "confirm identity",
                    "update payment",
                    "limited time",
                    "act now",
                    "claim prize",
                    "congratulations won",
                    "bank alert",
                    "urgent action",
                    "refund pending",
                    "tax return",
                    "nigerian prince",
                    "wire transfer",
                    "money transfer",
                    "free money",
                    "guaranteed income"
                )

                val existingCount = db.spamKeywordDao().getActiveKeywords().size
                if (existingCount == 0) {
                    val keywords = defaultKeywords.mapIndexed { index, keyword ->
                        com.FanMessenGer.data.model.SpamKeyword(
                            keyword = keyword,
                            severity = if (index < 5) 3 else if (index < 10) 2 else 1
                        )
                    }
                    db.spamKeywordDao().insertKeywords(keywords)
                    Timber.d("Initialized ${keywords.size} default spam keywords")
                }
            }
        }
    }
}
