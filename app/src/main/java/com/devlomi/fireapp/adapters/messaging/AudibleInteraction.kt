package com.devlomi.fireapp.adapters.messaging

import com.devlomi.fireapp.model.realms.Message

interface AudibleInteraction {
    fun onSeek(message:Message,progress:Int,max:Int)
}