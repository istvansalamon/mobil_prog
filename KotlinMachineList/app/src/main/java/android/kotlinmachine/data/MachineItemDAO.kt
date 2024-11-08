package android.kotlinmachine.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
/*
Itt az adatbázis műveletek találhatóak.
Új adattagkor (új ShippingItem adattag), nem szükséges módosítani itt.
 */
@Dao
interface MachineItemDAO {

    //Az összes listázása
    @Query("SELECT * FROM machineitem")
    fun findAllItems(): List<MachineItem>

    //Egy elem beszúrása
    @Insert
    fun insertItem(item: MachineItem): Long
    //Egy törlése
    @Delete
    fun deleteItem(item: MachineItem)
    //Egy módosítása
    @Update
    fun updateItem(item: MachineItem)

}
