package com.rayworks.transactionkeeper

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import java.util.LinkedList
import java.util.Locale

/**
 * A non-UI related Fragment as a state persistence monitor.
 *
 *
 * https://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
 */
class PlaceHolderFragment : Fragment() {

    private val taskBuffer = LinkedList<(activity: FragmentActivity) -> Unit>()
    private lateinit var host: FragmentActivity

    /***
     * Whether we need to retain its instance.
     */
    var retainedEnabled: Boolean = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // just bounded with the host Activity
        host = activity!!

        Log.i(sTAG, "host created : " + host)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = retainedEnabled
    }

    override fun onResume() {
        super.onResume()
        playback()
    }

    /**
     * * Executes Fragment related operation safely.
     *
     * @param runnable
     */
    fun tryWithAction(runnable: (activity: FragmentActivity) -> Unit) {
        val fragmentManager = host.supportFragmentManager

        if (fragmentManager.isStateSaved) {
            Log.i(sTAG, "<<< app state persisted already, delay the action.")
            collect(runnable)
        } else {
            runnable.invoke(host)
        }
    }

    private fun playback() {
        while (!taskBuffer.isEmpty()) {
            Log.i(sTAG, "<<< replay the action now")

            val runnable = taskBuffer.pop()
            runnable.invoke(host)
        }
    }

    private fun collect(runnable: (activity: FragmentActivity) -> Unit) {
        Log.i(sTAG, String.format(Locale.ENGLISH, "<<< collect the action %s", runnable))
        taskBuffer.add(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(sTAG, "host destroyed")
        taskBuffer.clear()
    }

    companion object {
        val sTAG = "X_PlaceHolderFragment"
    }
}
