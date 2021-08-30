package com.wasim.socketdemo.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val oddNumber=MutableLiveData<Int>()
    private val evenNumber=MutableLiveData<Int>()
    private val oddCounter=MutableLiveData<Int>()
    private val evenCounter=MutableLiveData<Int>()
    fun getOddNumberLiveData():MutableLiveData<Int>{
        return oddNumber
    }
    fun getEvenNumberLiveData():MutableLiveData<Int>{
        return evenNumber
    }
    fun getOddCounterLiveData():MutableLiveData<Int>{
        return oddCounter
    }
    fun getEvenCounterLiveData():MutableLiveData<Int>{
        return evenCounter
    }
    fun updateNumberLiveData(num:Int){
        GlobalScope.launch {
            if (num%2==0){
                evenNumber.postValue(num)
                evenCounter.postValue(if(evenCounter.value!=null) evenCounter.value!!.plus(1) else 1)
            }else{
                oddNumber.postValue(num)
                oddCounter.postValue(if(oddCounter.value!=null) oddCounter.value!!.plus(1) else 1)
            }
        }
    }
}