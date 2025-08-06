package com.example.photoapp.core.database.data.entities

import androidx.room.*

data class Uzytkownik(val id: String?,
                 val userId: String,
                 val displayName: String,
                 val createdAt: String,
                 val lastLogin: String,
                 val plan: String,
                 val isActive: Boolean,
                 val supportNotes: String,
                 val invoiceCount: Long,
                 val email: String,
                 val phone: String,

                 ){
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf("user_id" to this.userId,
            "display_name" to this.displayName,
            "created_at" to this.createdAt,
            "last_login" to this.lastLogin,
            "plan" to this.plan,
            "is_active" to this.isActive,
            "support_notes" to this.supportNotes,
            "invoice_count" to this.invoiceCount,
            "email" to this.email,
            "phone" to this.phone,
        )
    }

}