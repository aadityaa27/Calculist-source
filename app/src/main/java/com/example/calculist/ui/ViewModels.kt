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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListsViewModel(private val repo: CalcRepository) : ViewModel() {

    val lists: StateFlow<List<CalcList>> = repo.observeLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun itemsFor(listId: Long): Flow<List<ListItem>> = repo.observeItems(listId)

    fun createList(name: String) = viewModelScope.launch {
        repo.createList(name.trim())
    }

    fun deleteList(list: CalcList) = viewModelScope.launch {
        repo.deleteList(list)
    }
}

class CalculatorViewModel(
    private val repo: CalcRepository,
    private val listId: Long
) : ViewModel() {

    val listName: StateFlow<String> = repo.observeListName(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val items: StateFlow<List<ListItem>> = repo.observeItems(listId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stats: StateFlow<ListStats> = items
        .map { Calculator.computeStats(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListStats())

    fun addItem(name: String, value: Double, quantity: Double, notes: String) =
        viewModelScope.launch {
            repo.createItem(listId, name.trim(), value, quantity, notes.trim())
        }

    fun updateItem(item: ListItem) = viewModelScope.launch {
        repo.updateItem(item)
    }

    fun deleteItem(item: ListItem) = viewModelScope.launch {
        repo.deleteItem(item)
    }

    fun duplicateItem(item: ListItem) = viewModelScope.launch {
        repo.duplicateItem(item)
    }

    fun setCompleted(item: ListItem, completed: Boolean) = viewModelScope.launch {
        repo.setCompleted(item, completed)
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
