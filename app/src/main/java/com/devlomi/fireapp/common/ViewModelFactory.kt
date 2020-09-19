package com.devlomi.fireapp.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(application: Application) :
        ViewModelProvider.AndroidViewModelFactory(application)