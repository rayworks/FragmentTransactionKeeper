package com.rayworks.fragmenttransactionkeeper

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.rayworks.transactionkeeper.PlaceHolderFragment
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_main.content_parent
import kotlinx.android.synthetic.main.content_main.text_default

class MainActivity : AppCompatActivity() {

    private lateinit var placeHolderFragment: PlaceHolderFragment

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
                    content_parent.postDelayed({
                        showNewFragment(TextFragment())
                    }, 5000)
                }.show()
        }

        var fragment = supportFragmentManager.findFragmentByTag(PlaceHolderFragment.sTAG)
        if (fragment == null) {
            placeHolderFragment = PlaceHolderFragment()
            supportFragmentManager.beginTransaction()
                .add(placeHolderFragment, PlaceHolderFragment.sTAG)
                .commit()
        } else {
            placeHolderFragment = fragment as PlaceHolderFragment
        }
    }

    private fun showNewFragment(textFragment: TextFragment) {
        text_default.visibility = View.GONE

        placeHolderFragment.tryWithAction { activity ->
            kotlin.run {
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
