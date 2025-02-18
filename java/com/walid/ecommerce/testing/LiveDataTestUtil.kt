package com.walid.ecommerce.testing

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch

fun <T> LiveData<T>.getOrAwaitValue(): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    // Observe LiveData
    this.observeForever(observer)

    // Wait for the data to be set
    latch.await()

    return data ?: throw NullPointerException("LiveData value was null")
}
