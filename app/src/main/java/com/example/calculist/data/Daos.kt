package com.example.calculist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {

    @Query("SELECT * FROM lists ORDER BY createdAt DESC")
    fun allLists(): Flow<List<CalcList>>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun listById(id: Long): Flow<CalcList?>

    @Insert
    suspend fun insert(list: CalcList): Long

    @Update
    suspend fun update(list: CalcList)

    @Delete
    suspend fun delete(list: CalcList)
}

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE listId = :listId ORDER BY createdAt ASC")
    fun itemsFor(listId: Long): Flow<List<ListItem>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun itemById(itemId: Long): ListItem?

    @Insert
    suspend fun insert(item: ListItem): Long

    @Update
    suspend fun update(item: ListItem)

    @Delete
    suspend fun delete(item: ListItem)
}
