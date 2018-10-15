package com.rayworks.transactionkeeper

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log

class TransactionKeeper(private val lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private lateinit var placeHolderFragment: PlaceHolderFragment

    init {
        if (!(lifecycleOwner is FragmentActivity || lifecycleOwner is Fragment)) {
            throw IllegalArgumentException(
                "LifecycleOwner must be a 'FragmentActivity' or a 'FragmentActivity'"
            )
        }

        lifecycleOwner.lifecycle.addObserver(this)
    }

    /***
     * Executes Fragment related transactions, respecting app's saved State if any.
     */
    fun execute(runnable: (activity: FragmentActivity) -> Unit) {
        placeHolderFragment.tryWithAction(runnable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun doOnCreate() {
        Log.i("TK", " >>> doOnCreate")

        val manager = getFragmentManager(lifecycleOwner)
        manager.let {
            val fragment = it.findFragmentByTag(PlaceHolderFragment.sTAG)

            if (fragment == null) {
                placeHolderFragment = PlaceHolderFragment()
                placeHolderFragment.retainedEnabled = true

                it.beginTransaction()
                    .add(placeHolderFragment, PlaceHolderFragment.sTAG)
                    .commit()
            } else {
                placeHolderFragment = fragment as PlaceHolderFragment
            }
        }
    }

    private fun getFragmentManager(lifecycleOwner: LifecycleOwner): FragmentManager {
        return when (lifecycleOwner) {
            is FragmentActivity -> lifecycleOwner.supportFragmentManager
            is Fragment -> lifecycleOwner.childFragmentManager

            else -> throw IllegalStateException("Unable to get the FragmentManager")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun doOnDestroy() {
        Log.i("TK", " >>> doOnDestroy")
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}