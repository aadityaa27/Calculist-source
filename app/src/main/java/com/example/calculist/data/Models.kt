package com.example.calculist.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "lists")
data class CalcList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "items",
    foreignKeys = [ForeignKey(
        entity = CalcList::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("listId")]
)
data class ListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val name: String,
    val value: Double = 0.0,
    val quantity: Double = 1.0,
    val notes: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
