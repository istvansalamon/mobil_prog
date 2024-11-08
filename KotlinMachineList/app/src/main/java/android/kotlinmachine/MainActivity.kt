package android.kotlinmachine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.kotlinmachine.adapter.MachineAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.preference.PreferenceManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.kotlinmachine.data.AppDatabase
import android.kotlinmachine.data.MachineItem
import android.kotlinmachine.touch.MachineTouchHelperCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt


class MainActivity : AppCompatActivity(), MachineItemDialog.MachineItemHandler {
    companion object {
        val KEY_FIRST = "KEY_FIRST"
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var adapter: MachineAdapter
    /*
    Alkalmazás create-kor hívódik meg
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity-main.xml-t hozza be
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            MachineItemDialog().show(supportFragmentManager, "TAG_ITEM")
        }

        initRecyclerView()
            /*Új elem felvitelének vizsgálata, akkor hívódik meg, a dialógus címét állítja*/
        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(findViewById<View>(R.id.fab))
                    .setPrimaryText("New Machine Item")
                    .setSecondaryText("Tap here to create new Machine item")
                    .show()
        }


        saveThatItWasStarted()
    }

    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                KEY_FIRST, true
        )
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit()
                .putBoolean(KEY_FIRST, false)
                .apply()
    }




    private fun initRecyclerView() {
        val dbThread = Thread {
            //Lekéri az összes machine Item-et.
            val items = AppDatabase.getInstance(this).machineItemDAO().findAllItems()

            runOnUiThread{
                adapter = MachineAdapter(this, items)
                recyclerShopping.adapter = adapter

                val callback = MachineTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerShopping)
            }
        }
        dbThread.start()
    }
    /*Edit dialógus megnyitása*/
    fun showEditItemDialog(itemToEdit: MachineItem) {
        val editDialog = MachineItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

/*Új machine Item-kor beszúrjuk a DB-be a DAO segítségével*/
    override fun machineItemCreated(item: MachineItem) {
        val dbThread = Thread {
            val id = AppDatabase.getInstance(this@MainActivity).machineItemDAO().insertItem(item)

            item.itemId = id

            runOnUiThread{
                adapter.addItem(item)
            }
        }
        dbThread.start()
    }
/*Update-or módosítjuk a Machine Item-et a DAO segítségével*/
    override fun machineItemUpdated(item: MachineItem) {
        adapter.updateItem(item)

        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).machineItemDAO().updateItem(item)

            runOnUiThread { adapter.updateItem(item) }
        }
        dbThread.start()
    }
}
