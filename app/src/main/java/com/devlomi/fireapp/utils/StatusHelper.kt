package com.devlomi.fireapp.utils

import com.devlomi.fireapp.model.constants.MessageType
import com.devlomi.fireapp.model.constants.StatusType
import com.devlomi.fireapp.model.realms.Status

object StatusHelper {
    fun getStatusTypeDrawable(statusType: Int): Int {
        return MessageTypeHelper.getMessageTypeDrawable(mapStatusTypeToMessageType(statusType))
    }

    fun getStatusContent(status: Status): String {
        var type = mapStatusTypeToMessageType(status.type)
        return MessageTypeHelper.getTypeText(type)
    }

     fun mapStatusTypeToMessageType(statusType: Int): Int {
        return when (statusType) {
            StatusType.IMAGE -> {
                MessageType.SENT_IMAGE
            }
            StatusType.VIDEO -> {
                MessageType.SENT_VIDEO
            }
            else -> {
                MessageType.SENT_TEXT
            }
        }
    }
}