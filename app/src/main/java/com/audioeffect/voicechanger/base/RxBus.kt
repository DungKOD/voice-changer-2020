package com.audioeffect.voicechanger.base

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
object RxBus {

    private val publisher  = PublishSubject.create<Any>()
    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listener(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}