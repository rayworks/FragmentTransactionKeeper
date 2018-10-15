package com.rayworks.fragmenttransactionkeeper

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.rayworks.transactionkeeper.TransactionKeeper
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.text_default

class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    private lateinit var keeper: TransactionKeeper

    private val callback = Runnable {
        showNewFragment(TextFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setImageResource(android.R.drawable.ic_menu_edit)

        fab.setOnClickListener { view ->
            Snackbar.make(
                view,
                "Click 'Action' to replace content with a Fragment in 5s...", Snackbar.LENGTH_LONG
            )
                .setAction("Action") {
                    Toast.makeText(this@MainActivity, "Running ", Toast.LENGTH_SHORT).show()

                    // mock the async operation
                    handler.postDelayed(callback, 5000)
                }.show()
        }

        bindKeeper()
    }

    private fun bindKeeper() {
        keeper = TransactionKeeper(this)
    }

    private fun showNewFragment(textFragment: TextFragment) {
        keeper.execute { activity: FragmentActivity ->
            kotlin.run {
                text_default.visibility = View.GONE

                activity.supportFragmentManager.beginTransaction().add(
                    R.id.content_parent, textFragment
                ).commit()
            }
        }

        // NB: The origin logic will cause an exception.
        // Test steps:
        // 1. Click the action button
        // 2. press the HOME button immediately

        /*supportFragmentManager.beginTransaction().add(
            R.id.content_parent, textFragment
        ).commit()*/
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
