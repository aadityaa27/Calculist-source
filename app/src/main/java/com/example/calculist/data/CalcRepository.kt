package com.example.calculist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for all list/item persistence operations.
 * ViewModels talk to this repository; they never touch the DAOs directly.
 */
class CalcRepository(
    private val listDao: ListDao,
    private val itemDao: ItemDao
) {
    fun observeLists(): Flow<List<CalcList>> = listDao.allLists()

    fun observeListName(listId: Long): Flow<String> =
        listDao.listById(listId).map { it?.name.orEmpty() }

    fun observeItems(listId: Long): Flow<List<ListItem>> = itemDao.itemsFor(listId)

    suspend fun createList(name: String): Long =
        listDao.insert(CalcList(name = name))

    suspend fun deleteList(list: CalcList) = listDao.delete(list)

    suspend fun createItem(
        listId: Long,
        name: String,
        value: Double,
        quantity: Double,
        notes: String
    ): Long = itemDao.insert(
        ListItem(listId = listId, name = name, value = value, quantity = quantity, notes = notes)
    )

    suspend fun getItem(itemId: Long): ListItem? = itemDao.itemById(itemId)

    suspend fun updateItem(item: ListItem) = itemDao.update(item)

    suspend fun deleteItem(item: ListItem) = itemDao.delete(item)

    suspend fun duplicateItem(item: ListItem): Long =
        itemDao.insert(
            item.copy(
                id = 0,
                name = "${item.name} (copy)",
                createdAt = System.currentTimeMillis()
            )
        )

    suspend fun setCompleted(item: ListItem, completed: Boolean) =
        itemDao.update(item.copy(completed = completed))
}
