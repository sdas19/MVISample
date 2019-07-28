package com.example.mvisample.Model

import com.example.mvisample.View.MainViewState
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable


interface MainView :MvpView  {

    val imageIntent : Observable<Int> //imageIntent will use Integer index to get image from list

    fun render(viewState : MainViewState) //render function will render view based on view state
}