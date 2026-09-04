package com.example.calculist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculist.data.CalcList
import com.example.calculist.data.CalcRepository
import com.example.calculist.data.ListItem
import com.example.calculist.domain.Calculator
import com.example.calculist.domain.ItemFormErrors
import com.example.calculist.domain.ItemFormValidator
import com.example.calculist.domain.ListStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ListSortOption(val title: String) {
    NAME_AZ("Name (A-Z)"),
    NAME_ZA("Name (Z-A)"),
    VALUE_ASC("Value (0-9)"),
    VALUE_DESC("Value (9-0)"),
    DATE_CREATED_OLDEST("Date Created (Oldest First)"),
    DATE_CREATED_NEWEST("Date Created (Newest First)"),
    DATE_EDITED_OLDEST("Date Edited (Oldest First)"),
    DATE_EDITED_NEWEST("Date Edited (Newest First)"),
    CUSTOM("Custom")
}

enum class ItemFilterStatus(val title: String) {
    ALL("All"),
    ACTIVE("Pending"),
    COMPLETED("Done")
}

enum class ItemSortOption(val title: String) {
    DATE_CREATED_OLDEST("Date Created (Oldest First)"),
    DATE_CREATED_NEWEST("Date Created (Newest First)"),
    DATE_EDITED_OLDEST("Date Edited (Oldest First)"),
    DATE_EDITED_NEWEST("Date Edited (Newest First)"),
    NAME_AZ("Name (A-Z)"),
    NAME_ZA("Name (Z-A)"),
    VALUE_ASC("Value (0-9)"),
    VALUE_DESC("Value (9-0)"),
    CUSTOM("Custom")
}

class ListsViewModel(private val repo: CalcRepository) : ViewModel() {

    val lists: StateFlow<List<CalcList>> = repo.observeLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val searchQuery = MutableStateFlow("")
    val sortOption = MutableStateFlow(ListSortOption.DATE_CREATED_OLDEST)

    val filteredLists: StateFlow<List<CalcList>> = combine(
        lists,
        searchQuery,
        sortOption
    ) { allLists, query, sort ->
        val filtered = if (query.isBlank()) {
            allLists
        } else {
            allLists.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        when (sort) {
            ListSortOption.NAME_AZ -> filtered.sortedBy { it.name.lowercase() }
            ListSortOption.NAME_ZA -> filtered.sortedByDescending { it.name.lowercase() }
            ListSortOption.VALUE_ASC -> filtered.sortedBy { it.id } // lists don't store inline sum directly
            ListSortOption.VALUE_DESC -> filtered.sortedByDescending { it.id }
            ListSortOption.DATE_CREATED_OLDEST -> filtered.sortedBy { it.createdAt }
            ListSortOption.DATE_CREATED_NEWEST -> filtered.sortedByDescending { it.createdAt }
            ListSortOption.DATE_EDITED_OLDEST -> filtered.sortedBy { it.updatedAt }
            ListSortOption.DATE_EDITED_NEWEST -> filtered.sortedByDescending { it.updatedAt }
            ListSortOption.CUSTOM -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun itemsFor(listId: Long): Flow<List<ListItem>> = repo.observeItems(listId)

    fun createList(name: String, colorHex: String = "#FF453A", iconName: String = "list") = viewModelScope.launch {
        repo.createList(name.trim(), colorHex, iconName)
    }

    fun updateList(list: CalcList) = viewModelScope.launch {
        repo.updateList(list)
    }

    fun renameList(listId: Long, newName: String) = viewModelScope.launch {
        repo.renameList(listId, newName)
    }

    fun duplicateList(listId: Long) = viewModelScope.launch {
        repo.duplicateList(listId)
    }

    fun deleteList(list: CalcList) = viewModelScope.launch {
        repo.deleteList(list)
    }
}

class CalculatorViewModel(
    private val repo: CalcRepository,
    val listId: Long
) : ViewModel() {

    val currentList: StateFlow<CalcList?> = repo.observeList(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val listName: StateFlow<String> = currentList.map { it?.name.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val items: StateFlow<List<ListItem>> = repo.observeItems(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val searchQuery = MutableStateFlow("")
    val filterStatus = MutableStateFlow(ItemFilterStatus.ALL)
    val sortOption = MutableStateFlow(ItemSortOption.DATE_CREATED_OLDEST)
    val targetBudget = MutableStateFlow<Double?>(null)

    val showTotal = MutableStateFlow(true)
    val showAverage = MutableStateFlow(false)
    val showQuantity = MutableStateFlow(false)

    val filteredItems: StateFlow<List<ListItem>> = combine(
        items,
        searchQuery,
        filterStatus,
        sortOption
    ) { allItems, query, filter, sort ->
        var list = allItems

        // Filter by status
        list = when (filter) {
            ItemFilterStatus.ALL -> list
            ItemFilterStatus.ACTIVE -> list.filter { !it.completed }
            ItemFilterStatus.COMPLETED -> list.filter { it.completed }
        }

        // Search query
        if (query.isNotBlank()) {
            val q = query.trim()
            list = list.filter {
                it.name.contains(q, ignoreCase = true) || it.notes.contains(q, ignoreCase = true)
            }
        }

        // Sort option
        when (sort) {
            ItemSortOption.DATE_CREATED_OLDEST -> list.sortedBy { it.createdAt }
            ItemSortOption.DATE_CREATED_NEWEST -> list.sortedByDescending { it.createdAt }
            ItemSortOption.DATE_EDITED_OLDEST -> list.sortedBy { it.updatedAt }
            ItemSortOption.DATE_EDITED_NEWEST -> list.sortedByDescending { it.updatedAt }
            ItemSortOption.NAME_AZ -> list.sortedBy { it.name.lowercase() }
            ItemSortOption.NAME_ZA -> list.sortedByDescending { it.name.lowercase() }
            ItemSortOption.VALUE_ASC -> list.sortedBy { it.value * it.quantity }
            ItemSortOption.VALUE_DESC -> list.sortedByDescending { it.value * it.quantity }
            ItemSortOption.CUSTOM -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stats: StateFlow<ListStats> = items
        .map { Calculator.computeStats(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListStats())

    fun addItem(name: String, value: Double, quantity: Double, notes: String) =
        viewModelScope.launch {
            repo.createItem(listId, name.trim(), value, quantity, notes.trim())
            currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
        }

    fun updateItem(item: ListItem) = viewModelScope.launch {
        repo.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun adjustQuantity(item: ListItem, delta: Double) = viewModelScope.launch {
        repo.adjustQuantity(item, delta)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun deleteItem(item: ListItem) = viewModelScope.launch {
        repo.deleteItem(item)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun duplicateItem(item: ListItem) = viewModelScope.launch {
        repo.duplicateItem(item)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun setCompleted(item: ListItem, completed: Boolean) = viewModelScope.launch {
        repo.setCompleted(item, completed)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun setAllCompleted(completed: Boolean) = viewModelScope.launch {
        repo.setAllCompleted(listId, completed)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun clearCompleted() = viewModelScope.launch {
        repo.clearCompleted(listId)
        currentList.value?.let { repo.updateList(it.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun renameList(newName: String) = viewModelScope.launch {
        repo.renameList(listId, newName)
    }

    fun updateListDetails(name: String, colorHex: String, iconName: String) = viewModelScope.launch {
        currentList.value?.let {
            repo.updateList(it.copy(name = name.trim(), colorHex = colorHex, iconName = iconName, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteCurrentList(onDeleted: () -> Unit) = viewModelScope.launch {
        currentList.value?.let { repo.deleteList(it) }
        onDeleted()
    }

    fun formatShareText(): String {
        return Calculator.formatListAsText(listName.value.ifBlank { "List" }, items.value, stats.value)
    }
}

class ItemEditViewModel(
    private val repo: CalcRepository,
    private val listId: Long,
    private val itemId: Long
) : ViewModel() {

    private val _errors = MutableStateFlow(ItemFormErrors())
    val errors: StateFlow<ItemFormErrors> = _errors

    /** Loads the existing item for editing, or null when creating a new one. */
    suspend fun load(): ListItem? =
        if (itemId > 0) repo.getItem(itemId) else null

    fun clearErrors() {
        _errors.value = ItemFormErrors()
    }

    /**
     * Validates the form, then inserts (new item) or updates (existing item).
     * Calls [onSaved] only when the write succeeded.
     */
    suspend fun save(
        name: String,
        value: String,
        quantity: String,
        notes: String,
        completed: Boolean,
        onSaved: () -> Unit
    ) {
        val errors = ItemFormValidator.validate(name, value, quantity)
        _errors.value = errors
        if (errors.hasErrors) return

        val parsedValue = value.toDoubleOrNull() ?: return
        val parsedQuantity = quantity.toDoubleOrNull() ?: return

        if (itemId > 0) {
            val existing = repo.getItem(itemId) ?: return
            repo.updateItem(
                existing.copy(
                    name = name.trim(),
                    value = parsedValue,
                    quantity = parsedQuantity,
                    notes = notes.trim(),
                    completed = completed
                )
            )
        } else {
            repo.createItem(listId, name.trim(), parsedValue, parsedQuantity, notes.trim())
        }
        onSaved()
    }
}
