package com.FanMessenGer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.FanMessenGer.data.dao.*
import com.FanMessenGer.data.model.*

@Database(
    entities = [
        Message::class,
        Conversation::class,
        BlockedContact::class,
        SpamKeyword::class,
        SpamPattern::class,
        FirewallRule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun blockedContactDao(): BlockedContactDao
    abstract fun spamKeywordDao(): SpamKeywordDao
    abstract fun firewallRuleDao(): FirewallRuleDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "message_plus_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
