package com.rayworks.transactionkeeper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.Locale;

/**
 * A non-UI related Fragment as a state persistence monitor.
 *
 * <p>https://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
 */
public class PlaceHolderFragment extends Fragment {
    public static final String sTAG = "X_PlaceHolderFragment";

    private LinkedList<UiRunnable> taskBuffer = new LinkedList<>();
    private FragmentActivity host;

    public PlaceHolderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // just bounded
        host = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        playback();
    }

    /**
     * * Executes Fragment related operation safely.
     *
     * @param runnable {@link UiRunnable}
     */
    public void tryWithAction(UiRunnable runnable) {
        if (host != null) {
            final FragmentManager fragmentManager = host.getSupportFragmentManager();

            if (fragmentManager.isStateSaved()) {
                Log.i(sTAG, "<<< app state persisted already, delay the action.");
                collect(runnable);
            } else {
                runnable.run(host);
            }
        } else {
            collect(runnable);
        }
    }

    private void playback() {
        while (!taskBuffer.isEmpty()) {
            Log.i(sTAG, "<<< replay the action now");

            UiRunnable runnable = taskBuffer.pop();
            runnable.run(host);
        }
    }

    private void collect(UiRunnable runnable) {
        Log.i(sTAG, String.format(Locale.ENGLISH, "<<< collect the action %s", runnable));
        taskBuffer.add(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        host = null;
    }

    public interface UiRunnable {
        /**
         * * Executes the task with current bounded {@link FragmentActivity}
         *
         * @param activity
         */
        void run(FragmentActivity activity);
    }
}
