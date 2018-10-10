package com.coniferproductions.positions

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PositionsListViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var positions: MutableLiveData<List<Position>>

    private val positionRepository: PositionRepository
    internal val allPositions: LiveData<List<Position>>

    init {
        positionRepository = PositionRepository(application)
        allPositions = positionRepository.getAllPositions()
    }

    fun insert(position: Position) {
        positionRepository.insert(position)
    }
}