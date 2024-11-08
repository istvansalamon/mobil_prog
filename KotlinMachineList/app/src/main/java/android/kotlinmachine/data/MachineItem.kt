package android.kotlinmachine.data

import java.io.Serializable

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "machineitem")
data class MachineItem(@PrimaryKey(autoGenerate = true) var itemId: Long?,
                       @ColumnInfo(name = "name") var name: String,
                       @ColumnInfo(name = "quantity") var quantity: Int,
                       @ColumnInfo(name = "aktiv") var aktiv: Boolean,
                       @ColumnInfo(name = "description") var descript: String,
                       @ColumnInfo(name = "fault") var fault: String
) : Serializable
