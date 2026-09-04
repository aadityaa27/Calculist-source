package com.example.calculist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculist.calcViewModel
import com.example.calculist.data.CalcList
import com.example.calculist.data.ListItem
import com.example.calculist.data.SettingsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListsScreen(
    onOpenList: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val vm = calcViewModel<ListsViewModel> { ListsViewModel(it.repository) }
    val allLists by vm.lists.collectAsState()
    val filteredLists by vm.filteredLists.collectAsState()
    val sortOption by vm.sortOption.collectAsState()

    val context = LocalContext.current
    val settings = remember { SettingsManager.get(context) }
    val currencySymbol by settings.currencySymbol.collectAsState()

    var showNewListDialog by remember { mutableStateOf(false) }
    var listToEdit by remember { mutableStateOf<CalcList?>(null) }
    var listToDelete by remember { mutableStateOf<CalcList?>(null) }
    var listOptionsTarget by remember { mutableStateOf<CalcList?>(null) }
    var showSortSheet by remember { mutableStateOf(false) }

    val darkBg = Color(0xFF000000)
    val buttonBg = Color(0xFF1C1C1E)
    val cardBg = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF8E8E93)
    val orangeAccent = Color(0xFFFF9F0A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Navigation Bar (Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings gear button in circular dark container
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Sort button (arrows up/down) in circular dark container
                IconButton(
                    onClick = { showSortSheet = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Sort",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Title Area: "CalcuList"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "CalcuList",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                if (allLists.isEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No lists added yet",
                        color = textSecondary,
                        fontSize = 16.sp
                    )
                }
            }

            // Lists list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLists, key = { it.id }) { list ->
                    val itemsFlow = remember(list.id) { vm.itemsFor(list.id) }
                    val items by itemsFlow.collectAsState(initial = emptyList())
                    val activeItems = items.filter { !it.completed }
                    val totalSum = activeItems.sumOf { it.value * it.quantity }
                    val listColor = ListVisuals.colorFromHex(list.colorHex)
                    val listIcon = ListVisuals.iconFromId(list.iconName)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .combinedClickable(
                                onClick = { onOpenList(list.id) },
                                onLongClick = { listOptionsTarget = list }
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored circular icon badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(listColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = listIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Title & items count
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = list.name,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val dateStr = remember(list.createdAt) {
                                SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(list.createdAt))
                            }
                            Text(
                                text = "$dateStr · ${items.size} ${if (items.size == 1) "item" else "items"}",
                                color = textSecondary,
                                fontSize = 13.sp
                            )
                        }

                        // Active total value
                        if (items.isNotEmpty()) {
                            Text(
                                text = Formatters.formatAmount(totalSum, currencySymbol),
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF48484A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bottom Bar: "+ Add List" in bottom right (Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showNewListDialog = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+ Add List",
                        color = orangeAccent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Sort Action Sheet (Screenshot 2)
        if (showSortSheet) {
            val sortItems = listOf(
                ActionSheetItem("Name (A-Z)") {
                    vm.sortOption.value = ListSortOption.NAME_AZ
                    showSortSheet = false
                },
                ActionSheetItem("Name (Z-A)") {
                    vm.sortOption.value = ListSortOption.NAME_ZA
                    showSortSheet = false
                },
                ActionSheetItem("Value (0-9)") {
                    vm.sortOption.value = ListSortOption.VALUE_ASC
                    showSortSheet = false
                },
                ActionSheetItem("Value (9-0)") {
                    vm.sortOption.value = ListSortOption.VALUE_DESC
                    showSortSheet = false
                },
                ActionSheetItem("Date Created (Oldest First)") {
                    vm.sortOption.value = ListSortOption.DATE_CREATED_OLDEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Created (Newest First)") {
                    vm.sortOption.value = ListSortOption.DATE_CREATED_NEWEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Edited (Oldest First)") {
                    vm.sortOption.value = ListSortOption.DATE_EDITED_OLDEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Edited (Newest First)") {
                    vm.sortOption.value = ListSortOption.DATE_EDITED_NEWEST
                    showSortSheet = false
                },
                ActionSheetItem("Custom") {
                    vm.sortOption.value = ListSortOption.CUSTOM
                    showSortSheet = false
                }
            )
            IosActionSheet(
                items = sortItems,
                onDismiss = { showSortSheet = false }
            )
        }

        // New List Dialog (Screenshot 3)
        if (showNewListDialog) {
            NewListDialog(
                title = "New List",
                onDismiss = { showNewListDialog = false },
                onSave = { name, colorHex, iconId ->
                    vm.createList(name, colorHex, iconId)
                    showNewListDialog = false
                }
            )
        }

        // Edit List Dialog
        listToEdit?.let { target ->
            NewListDialog(
                title = "Edit List",
                initialName = target.name,
                initialColorHex = target.colorHex,
                initialIconId = target.iconName,
                onDismiss = { listToEdit = null },
                onSave = { name, colorHex, iconId ->
                    vm.updateList(
                        target.copy(
                            name = name,
                            colorHex = colorHex,
                            iconName = iconId,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                    listToEdit = null
                }
            )
        }

        // Long Press Context Menu Action Sheet
        listOptionsTarget?.let { target ->
            val options = listOf(
                ActionSheetItem("Open") {
                    listOptionsTarget = null
                    onOpenList(target.id)
                },
                ActionSheetItem("Edit List...") {
                    listOptionsTarget = null
                    listToEdit = target
                },
                ActionSheetItem("Duplicate") {
                    listOptionsTarget = null
                    vm.duplicateList(target.id)
                },
                ActionSheetItem("Delete List", isDestructive = true) {
                    listOptionsTarget = null
                    listToDelete = target
                }
            )
            IosActionSheet(
                items = options,
                onDismiss = { listOptionsTarget = null }
            )
        }

        // Confirm Delete Dialog
        listToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { listToDelete = null },
                title = { Text("Delete List?", color = Color.White) },
                text = {
                    Text(
                        "Are you sure you want to delete \"${target.name}\"? All its items will be permanently removed.",
                        color = textSecondary
                    )
                },
                containerColor = cardBg,
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.deleteList(target)
                            listToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color(0xFFFF453A), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { listToDelete = null }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}
