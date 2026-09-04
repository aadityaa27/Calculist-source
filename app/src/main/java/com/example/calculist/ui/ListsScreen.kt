package com.example.calculist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculist.calcViewModel
import com.example.calculist.data.CalcList
import com.example.calculist.domain.Calculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(onOpenList: (Long) -> Unit) {
    val vm = calcViewModel<ListsViewModel> { ListsViewModel(it.repository) }
    val lists by vm.lists.collectAsState()
    var showNewListDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CalcuList") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewListDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New list")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(lists, key = { it.id }) { list ->
                ListCard(list = list, vm = vm, onOpen = { onOpenList(list.id) })
            }
        }
    }

    if (showNewListDialog) {
        NewListDialog(
            onDismiss = { showNewListDialog = false },
            onCreate = { name ->
                vm.createList(name)
                showNewListDialog = false
            }
        )
    }
}

@Composable
private fun ListCard(list: CalcList, vm: ListsViewModel, onOpen: () -> Unit) {
    val items by vm.itemsFor(list.id).collectAsState(initial = emptyList())
    val stats = Calculator.computeStats(items)

    Card(
        onClick = onOpen,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(list.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Total: ${Formatters.money(stats.total)}  ·  ${stats.itemCount} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { vm.deleteList(list) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete list")
            }
        }
    }
}

@Composable
private fun NewListDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New list") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("List name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onCreate(name.trim()) }
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
