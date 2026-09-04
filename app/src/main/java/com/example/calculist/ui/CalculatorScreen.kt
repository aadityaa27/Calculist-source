package com.example.calculist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.calculist.calcViewModel
import com.example.calculist.data.ListItem
import com.example.calculist.domain.Calculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    listId: Long,
    onBack: () -> Unit,
    onAddItem: () -> Unit,
    onEditItem: (Long) -> Unit
) {
    val vm = calcViewModel<CalculatorViewModel>(key = "calc_$listId") {
        CalculatorViewModel(it.repository, listId)
    }
    val listName by vm.listName.collectAsState()
    val items by vm.items.collectAsState()
    val stats by vm.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listName.ifBlank { "Calculator" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddItem) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SummaryRow(stats = stats)

            if (items.isEmpty()) {
                Text(
                    "No items yet. Tap \"Add item\" to create one.",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(Modifier.weight(1f)) {
                items(items, key = { it.id }) { item ->
                    ItemRow(
                        item = item,
                        onToggleCompleted = { vm.setCompleted(item, it) },
                        onEdit = { onEditItem(item.id) },
                        onDuplicate = { vm.duplicateItem(item) },
                        onDelete = { vm.deleteItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(stats: com.example.calculist.domain.ListStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCell(label = "Total", value = Formatters.money(stats.total))
            StatCell(label = "Average", value = Formatters.money(stats.average))
            StatCell(label = "Quantity", value = Formatters.compact(stats.totalQuantity))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ItemRow(
    item: ListItem,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val contribution = Calculator.contribution(item)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.completed,
            onCheckedChange = onToggleCompleted
        )
        Column(Modifier.weight(1f)) {
            Text(
                item.name,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.completed) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            if (item.notes.isNotBlank()) {
                Text(
                    item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Text(
                "${Formatters.compact(item.value)} × ${Formatters.compact(item.quantity)} = ${Formatters.money(contribution)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit item")
        }
        IconButton(onClick = onDuplicate) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate item")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete item")
        }
    }
}
