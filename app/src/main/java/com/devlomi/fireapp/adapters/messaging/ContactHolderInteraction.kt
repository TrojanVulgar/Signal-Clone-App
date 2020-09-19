package com.devlomi.fireapp.adapters.messaging

import com.devlomi.fireapp.model.realms.RealmContact

interface ContactHolderInteraction {
    fun onMessageClick(contact:RealmContact)
    fun onAddContactClick(contact:RealmContact)
}