package com.devlomi.fireapp

import io.reactivex.disposables.CompositeDisposable

interface Base {
    val disposables:CompositeDisposable
}