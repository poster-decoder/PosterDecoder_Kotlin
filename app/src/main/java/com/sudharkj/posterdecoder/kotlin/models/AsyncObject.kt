package com.sudharkj.posterdecoder.kotlin.models

interface AsyncObject<ReturnType> {
    fun process(): ReturnType
}