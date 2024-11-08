package android.kotlinmachine

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.kotlinmachine.data.MachineItem
import kotlinx.android.synthetic.main.dialog_create_item.view.*

/*
Ez a dialógus ablak szolgál az új Shipping Item felvitelére, és a meglevő Shopping Item módosítására
 */

class MachineItemDialog : DialogFragment() {

    private lateinit var machineItemHandler: MachineItemHandler
    //Shopping Item elemek text-ben, ide szükséges a bővítés a Shopping Item új adattagja esetén
    private lateinit var etItem: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etDescript: EditText
    private lateinit var etFault: EditText

    interface MachineItemHandler {
        fun machineItemCreated(item: MachineItem)

        fun machineItemUpdated(item: MachineItem)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is MachineItemHandler) {
            machineItemHandler = context
        } else {
            throw RuntimeException("The Activity does not implement the ShoppingItemHandler interface")
        }
    }
/*Új Shopping Item felvitelekor ez hívódik meg. A felirat a New Item lesz*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New Item")

        initDialogContent(builder)

        builder.setPositiveButton("OK") { dialog, which ->
            // keep it empty
        }
        return builder.create()
    }

    private fun initDialogContent(builder: AlertDialog.Builder) {
        /*etItem = EditText(activity)
        builder.setView(etItem)*/

        //dialog_create_item.xml-el dolgozunk
        val rootView = requireActivity().layoutInflater.inflate(R.layout.dialog_create_item, null)
        //Shopping Item tagok az xml-ből (EditText elemek)
        //Itt is szükséges a bővítés új Shopping Item adattag esetén
        etItem = rootView.etName
        etQuantity = rootView.etQuantity
        etDescript = rootView.etDescript
        etFault = rootView.etFault
        builder.setView(rootView)
        //Megnézzük, hogy kapott-e argumentumot (a fő ablakból), ha igen, akkor az adattagokat beállítjuk erre
        // tehát az Edittext-ek kapnak értéket, és az ablak címét beállítjuk
        val arguments = this.arguments
        if (arguments != null &&
                arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            val item = arguments.getSerializable(
                    MainActivity.KEY_ITEM_TO_EDIT) as MachineItem
            //Itt is szükséges a bővítés új Shopping Item adattag esetén
            etItem.setText(item.name)
            etQuantity.setText(item.quantity.toString())
            etDescript.setText(item.descript)
            etFault.setText(item.fault)


            builder.setTitle("Edit Machine")
        }
    }


    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
         //OK gomb a dialógus ablakon
        //vizsgálja az eseménykezelője, hogy a dialógus ablak kapott-e paramétereket
        //Ha kapott, akkor a handleItemEdit() hívódik meg (edit)
        //Ha nem kapott, akor a handleItemCreate() hívódik meg (create)
        positiveButton.setOnClickListener {
            if (etItem.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null &&
                        arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else {
                etItem.error = "This field can not be empty"
            }
        }
    }
    //Új elem esetén hvódik meg, egy új ShoppingItem-et hoz létre
    //az itemId azért null, mert a DB adja a kulcsot
    //Itt is szükséges a bővítés új Shopping Item adattag esetén
    private fun handleItemCreate() {
        machineItemHandler.machineItemCreated(MachineItem(
                null,
                etItem.text.toString(),
                etQuantity.text.toString().toInt(),
                false,
                etDescript.text.toString(),
                etFault.text.toString()
        ))
    }
    //Edit esetén hívódik meg, az edit-et csinálja, paraméterként átadja az adatokat
    //Itt is szükséges a bővítés új Shopping Item adattag esetén
    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT) as MachineItem
        itemToEdit.name = etItem.text.toString()
        itemToEdit.quantity = etQuantity.text.toString().toInt()
        itemToEdit.descript=etDescript.text.toString()
        itemToEdit.fault=etFault.text.toString()

        machineItemHandler.machineItemUpdated(itemToEdit)
    }
}
