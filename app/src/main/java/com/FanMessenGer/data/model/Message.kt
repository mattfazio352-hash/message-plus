package com.FanMessenGer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: Long,
    val phoneNumber: String,
    val contactName: String? = null,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isSent: Boolean = false,
    val isDelivered: Boolean = false,
    val isFailed: Boolean = false,
    val messageType: String = "SMS", // SMS, MMS, RCS
    val attachmentUri: String? = null,
    val attachmentMimeType: String? = null,
    val attachmentSize: Long = 0,
    val isSpam: Boolean = false,
    val spamReason: String? = null,
    val isBlocked: Boolean = false
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val contactName: String? = null,
    val lastMessage: String? = null,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val contactAvatar: String? = null
)

@Entity(tableName = "blocked_contacts")
data class BlockedContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val contactName: String? = null,
    val reason: String? = null,
    val blockedDate: Long = System.currentTimeMillis(),
    val blockType: String = "MANUAL" // MANUAL, SPAM, FIREWALL
)

@Entity(tableName = "spam_keywords")
data class SpamKeyword(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val severity: Int = 1, // 1 = low, 2 = medium, 3 = high
    val isActive: Boolean = true
)

@Entity(tableName = "spam_patterns")
data class SpamPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pattern: String,
    val regex: String,
    val severity: Int = 1,
    val isActive: Boolean = true,
    val description: String? = null
)

@Entity(tableName = "firewall_rules")
data class FirewallRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String? = null,
    val keyword: String? = null,
    val action: String = "BLOCK", // BLOCK, WARN, ALLOW
    val ruleType: String = "MALWARE", // MALWARE, PHISHING, SCAM, FRAUD
    val isActive: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)

data class MessageWithStatus(
    val message: Message,
    val status: MessageStatus
)

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}
