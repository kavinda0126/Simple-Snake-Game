package com.example.snake.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityData: ViewModel() {
    private val _score=MutableLiveData<Int>().apply{value=0}

    val score: LiveData<Int> = _score

     fun increment(){
         _score.value=_score.value!!+1
     }


}