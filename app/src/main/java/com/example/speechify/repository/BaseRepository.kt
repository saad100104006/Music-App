package com.example.speechify.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseRepository {
    protected val loadingState: MutableLiveData<Boolean> = MutableLiveData(false)
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    open fun clearResources() {
        compositeDisposable.clear()
    }

    fun getLoadingState(): LiveData<Boolean> = loadingState
}