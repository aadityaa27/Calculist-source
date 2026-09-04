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

    fun observeList(listId: Long): Flow<CalcList?> = listDao.listById(listId)

    fun observeItems(listId: Long): Flow<List<ListItem>> = itemDao.itemsFor(listId)

    suspend fun createList(
        name: String,
        colorHex: String = "#FF453A",
        iconName: String = "list"
    ): Long = listDao.insert(CalcList(name = name.trim(), colorHex = colorHex, iconName = iconName))

    suspend fun updateList(list: CalcList) = listDao.update(list)

    suspend fun renameList(listId: Long, newName: String) {
        val existing = listDao.getListById(listId) ?: return
        listDao.update(existing.copy(name = newName.trim(), updatedAt = System.currentTimeMillis()))
    }

    suspend fun duplicateList(listId: Long): Long {
        val original = listDao.getListById(listId) ?: return -1L
        val newListId = listDao.insert(
            CalcList(
                name = "${original.name} (Copy)",
                colorHex = original.colorHex,
                iconName = original.iconName
            )
        )
        val items = itemDao.getItemsForList(listId)
        if (items.isNotEmpty()) {
            val copiedItems = items.map {
                it.copy(
                    id = 0,
                    listId = newListId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
            itemDao.insertAll(copiedItems)
        }
        return newListId
    }

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

    suspend fun adjustQuantity(item: ListItem, delta: Double) {
        val newQty = (item.quantity + delta).coerceAtLeast(0.1)
        // round to 2 decimal places to avoid floating point drift
        val rounded = Math.round(newQty * 100.0) / 100.0
        itemDao.update(item.copy(quantity = rounded))
    }

    suspend fun clearCompleted(listId: Long) = itemDao.deleteCompleted(listId)

    suspend fun setAllCompleted(listId: Long, completed: Boolean) =
        itemDao.setAllCompleted(listId, completed)
}
