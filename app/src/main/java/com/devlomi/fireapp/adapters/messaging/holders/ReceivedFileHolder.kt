package com.devlomi.fireapp.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.devlomi.fireapp.R
import com.devlomi.fireapp.adapters.messaging.holders.base.BaseReceivedHolder
import com.devlomi.fireapp.common.extensions.setHidden
import com.devlomi.fireapp.model.constants.DownloadUploadStat
import com.devlomi.fireapp.model.realms.Message
import com.devlomi.fireapp.model.realms.User
import com.devlomi.fireapp.utils.Util

class ReceivedFileHolder(context: Context, itemView: View) : BaseReceivedHolder(context, itemView) {

    private val fileIcon: ImageView
    private val tvFileName: TextView
    private val tvFileExtension: TextView
    private val tvFileSize: TextView

    override fun bind(message: Message, user: User) {
        super.bind(message, user)
        //get file extension
        val fileExtension = Util.getFileExtensionFromPath(message.metadata).toUpperCase()
        tvFileExtension.text = fileExtension
        //set file name
        tvFileName.text = message.metadata

        //file size
        tvFileSize.text = message.fileSize

        fileIcon.setHidden(message.downloadUploadStat != DownloadUploadStat.SUCCESS, true)


    }

    init {
        fileIcon = itemView.findViewById(R.id.file_icon)
        tvFileName = itemView.findViewById(R.id.tv_file_name)
        tvFileExtension = itemView.findViewById(R.id.tv_file_extension)
        tvFileSize = itemView.findViewById(R.id.tv_file_size)
    }


}

