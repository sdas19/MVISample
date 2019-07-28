package com.example.mvisample.Model

interface PartialMainState {

    class Loading : PartialMainState
    class gotImageLink(var imageLink : String) : PartialMainState
    class Error(var error : Throwable) : PartialMainState
}