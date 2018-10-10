package com.coniferproductions.positions

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class PositionViewModel(application: Application): AndroidViewModel(application) {
    private val repository: PositionRepository

    init {
        repository = PositionRepository(application)
    }

    fun insert(position: Position) {
        repository.insert(position)
    }
}
