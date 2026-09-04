package com.example.calculist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calculist.calcViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    listId: Long,
    itemId: Long,
    onDone: () -> Unit
) {
    val vm = calcViewModel<ItemEditViewModel>(key = "item_${listId}_$itemId") {
        ItemEditViewModel(it.repository, listId, itemId)
    }

    var name by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    val errors by vm.errors.collectAsState()
    val scope = rememberCoroutineScope()
    val isEditing = itemId > 0

    LaunchedEffect(itemId) {
        vm.load()?.let { item ->
            name = item.name
            value = Formatters.compact(item.value)
            quantity = Formatters.compact(item.quantity)
            notes = item.notes
            completed = item.completed
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Item" else "Add Item") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    vm.clearErrors()
                },
                label = { Text("Name") },
                singleLine = true,
                isError = errors.name != null,
                supportingText = { errors.name?.let { errorText -> ErrorText(errorText) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = {
                    value = it
                    vm.clearErrors()
                },
                label = { Text("Value") },
                singleLine = true,
                isError = errors.value != null,
                supportingText = { errors.value?.let { errorText -> ErrorText(errorText) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    quantity = it
                    vm.clearErrors()
                },
                label = { Text("Quantity") },
                singleLine = true,
                isError = errors.quantity != null,
                supportingText = { errors.quantity?.let { errorText -> ErrorText(errorText) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = completed,
                    onCheckedChange = { completed = it }
                )
                Spacer(Modifier.height(0.dp))
                Text(
                    if (completed) "Completed" else "Incomplete",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        vm.save(name, value, quantity, notes, completed, onSaved = onDone)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Save changes" else "Add item")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(message, color = MaterialTheme.colorScheme.error)
}
