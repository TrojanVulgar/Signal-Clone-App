package com.devlomi.fireapp.adapters.messaging

import androidx.lifecycle.LiveData
import com.devlomi.fireapp.model.AudibleState

interface AudibleBase {
    var audibleState: LiveData<Map<String, AudibleState>>?
    var audibleInteraction:AudibleInteraction?
}