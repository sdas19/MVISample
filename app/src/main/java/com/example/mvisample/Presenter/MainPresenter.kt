package com.example.mvisample.Presenter

import com.example.mvisample.Model.MainView
import com.example.mvisample.Model.PartialMainState
import com.example.mvisample.Utils.DataSource
import com.example.mvisample.View.MainViewState
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class MainPresenter(internal var dataSource: DataSource) : MviBasePresenter<MainView, MainViewState>() {

    override fun bindIntents() {

        val gotData = intent(ViewIntentBinder<MainView, Int> { it.imageIntent })
            .switchMap { index ->
                dataSource.getImageLinkFromList(index)
                    .map { imageLink -> PartialMainState.gotImageLink(imageLink) as PartialMainState }
                    .startWith(PartialMainState.Loading())
                    .onErrorReturn { error -> PartialMainState.Error(error) }
                    .subscribeOn(Schedulers.io())
            }

        val initState = MainViewState(false,false,"",null)
        val initIntent = gotData.observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(initIntent.scan(
            initState,BiFunction<MainViewState,PartialMainState,MainViewState>{
                prevState,changedState -> this.viewStateReducer(prevState,changedState)
            }
        ))
        {
            obj,viewState -> obj.render(viewState)
        }
    }

    internal fun viewStateReducer(prevState : MainViewState, changedState : PartialMainState) : MainViewState{

        if(changedState is PartialMainState.Loading){
            prevState.isLoading = true
            prevState.isImageViewShow = false
        }

        if(changedState is PartialMainState.gotImageLink){
            prevState.isLoading = false
            prevState.isImageViewShow = true
            prevState.imageLink = changedState.imageLink
        }

        if(changedState is PartialMainState.Error){
            prevState.isLoading = false
            prevState.isImageViewShow = false
            prevState.error = changedState.error
        }

        return prevState
    }
}