package com.lockboxth.lockboxkiosk.service.firebase

import io.reactivex.*
import io.reactivex.subjects.PublishSubject

class NotificationsManager {

    private var notificationPublisher: PublishSubject<Any>? = null

    fun getPublisher(): PublishSubject<Any> {
        if (notificationPublisher == null) {
            notificationPublisher = PublishSubject.create()
        }
        return notificationPublisher!!
    }

    fun getNotificationObservable(): Observable<Any> {
        return getPublisher()
    }

    companion object {
        var instance: NotificationsManager? = null
            get() {
                if (field == null) field = NotificationsManager()
                return field
            }
            private set
    }
}