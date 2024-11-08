package android.kotlinmachine.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
/*
Elkészíti az adatbázist azaz a machine.db-t. a ShoppingItem alapján lesz a tábla
 */
@Database(entities = arrayOf(MachineItem::class), version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun machineItemDAO(): MachineItemDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "machine.db")
                        .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}