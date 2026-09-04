package com.example.calculist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculist.calcViewModel
import com.example.calculist.data.ListItem
import com.example.calculist.data.SettingsManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorScreen(
    listId: Long,
    onBack: () -> Unit,
    onAddItem: () -> Unit = {},
    onEditItem: (Long) -> Unit = {}
) {
    val vm = calcViewModel<CalculatorViewModel>(key = "calc_$listId") {
        CalculatorViewModel(it.repository, listId)
    }

    val currentList by vm.currentList.collectAsState()
    val listName by vm.listName.collectAsState()
    val items by vm.filteredItems.collectAsState()
    val stats by vm.stats.collectAsState()
    val sortOption by vm.sortOption.collectAsState()

    val showTotal by vm.showTotal.collectAsState()
    val showAverage by vm.showAverage.collectAsState()
    val showQuantity by vm.showQuantity.collectAsState()

    val context = LocalContext.current
    val settings = remember { SettingsManager.get(context) }
    val currencySymbol by settings.currencySymbol.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ListItem?>(null) }
    var itemOptionsTarget by remember { mutableStateOf<ListItem?>(null) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val darkBg = Color(0xFF000000)
    val buttonBg = Color(0xFF1C1C1E)
    val rowBg = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF8E8E93)
    val accentColor = currentList?.colorHex?.let { ListVisuals.colorFromHex(it) } ?: Color(0xFFFF453A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding()
                .widthIn(max = 680.dp)
                .align(Alignment.TopCenter)
        ) {
            // Top Bar: Back button < and More ... button (Screenshot 5)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button pill
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // More ... button pill
                IconButton(
                    onClick = { showMoreMenuSheet = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // List Title & Sort By Subtitle (Screenshot 5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = listName.ifBlank { "List" },
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sorted By: ${sortOption.title}",
                    color = textSecondary,
                    fontSize = 14.sp
                )
            }

            // Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items yet. Tap + Add Item below.",
                                color = textSecondary,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                items(items, key = { it.id }) { item ->
                    val totalItemValue = item.value * item.quantity

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(rowBg)
                            .combinedClickable(
                                onClick = {
                                    vm.setCompleted(item, !item.completed)
                                },
                                onLongClick = {
                                    itemOptionsTarget = item
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox circle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .then(
                                    if (item.completed) {
                                        Modifier
                                            .clip(CircleShape)
                                            .background(accentColor)
                                    } else {
                                        Modifier
                                            .border(2.dp, Color(0xFF48484A), CircleShape)
                                            .clip(CircleShape)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.completed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Item name and optional details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                color = if (item.completed) textSecondary else Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                textDecoration = if (item.completed) TextDecoration.LineThrough else null,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            val hasDetail = item.notes.isNotBlank()
                            val showQty = showQuantity || item.quantity > 1.0
                            if (hasDetail || showQty) {
                                Spacer(modifier = Modifier.height(2.dp))
                                val subtitle = buildString {
                                    if (hasDetail) append(item.notes)
                                    if (hasDetail && showQty) append(" · ")
                                    if (showQty) append("Qty: ${Formatters.compact(item.quantity)}")
                                }
                                Text(
                                    text = subtitle,
                                    color = textSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Item Amount (Screenshot 5: e.g. "707", "160")
                        Text(
                            text = Formatters.formatAmount(totalItemValue, currencySymbol),
                            color = if (item.completed) textSecondary else Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (item.completed) TextDecoration.LineThrough else null
                        )
                    }
                }
            }

            // Bottom Bar: Totals display on Left, "+ Add Item" on Right (Screenshot 5)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Total / Average summary
                Column {
                    if (showTotal) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Total: ",
                                color = textSecondary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = Formatters.formatAmount(stats.total, currencySymbol),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (showAverage && stats.itemCount > 0) {
                        Text(
                            text = "Avg: ${Formatters.formatAmount(stats.average, currencySymbol)}",
                            color = textSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Right: "+ Add Item" pill button matching list accent color
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showAddItemDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+ Add Item",
                            color = accentColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // More Options Action Sheet (Screenshot 5)
        if (showMoreMenuSheet) {
            val moreItems = listOf(
                ActionSheetItem("Formatting") {
                    showMoreMenuSheet = false
                    // Cycle or open currency
                    val nextCurrency = when (currencySymbol) {
                        "" -> "₹"
                        "₹" -> "$"
                        "$" -> "€"
                        "€" -> "£"
                        else -> ""
                    }
                    settings.setCurrency(nextCurrency)
                },
                ActionSheetItem("Sort By...") {
                    showMoreMenuSheet = false
                    showSortSheet = true
                },
                ActionSheetItem(if (showTotal) "Hide Total" else "Show Total...") {
                    vm.showTotal.value = !showTotal
                    showMoreMenuSheet = false
                },
                ActionSheetItem(if (showAverage) "Hide Average" else "Show Average...") {
                    vm.showAverage.value = !showAverage
                    showMoreMenuSheet = false
                },
                ActionSheetItem(if (showQuantity) "Hide Quantity" else "Show Quantity...") {
                    vm.showQuantity.value = !showQuantity
                    showMoreMenuSheet = false
                },
                ActionSheetItem("Reset Completed Items") {
                    vm.setAllCompleted(false)
                    showMoreMenuSheet = false
                },
                ActionSheetItem("Delete...", isDestructive = true) {
                    showMoreMenuSheet = false
                    showDeleteConfirmDialog = true
                }
            )
            IosActionSheet(
                items = moreItems,
                onDismiss = { showMoreMenuSheet = false }
            )
        }

        // Sort Action Sheet for List Items
        if (showSortSheet) {
            val sortItems = listOf(
                ActionSheetItem("Date Created (Oldest First)") {
                    vm.sortOption.value = ItemSortOption.DATE_CREATED_OLDEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Created (Newest First)") {
                    vm.sortOption.value = ItemSortOption.DATE_CREATED_NEWEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Edited (Oldest First)") {
                    vm.sortOption.value = ItemSortOption.DATE_EDITED_OLDEST
                    showSortSheet = false
                },
                ActionSheetItem("Date Edited (Newest First)") {
                    vm.sortOption.value = ItemSortOption.DATE_EDITED_NEWEST
                    showSortSheet = false
                },
                ActionSheetItem("Name (A-Z)") {
                    vm.sortOption.value = ItemSortOption.NAME_AZ
                    showSortSheet = false
                },
                ActionSheetItem("Name (Z-A)") {
                    vm.sortOption.value = ItemSortOption.NAME_ZA
                    showSortSheet = false
                },
                ActionSheetItem("Value (0-9)") {
                    vm.sortOption.value = ItemSortOption.VALUE_ASC
                    showSortSheet = false
                },
                ActionSheetItem("Value (9-0)") {
                    vm.sortOption.value = ItemSortOption.VALUE_DESC
                    showSortSheet = false
                },
                ActionSheetItem("Custom") {
                    vm.sortOption.value = ItemSortOption.CUSTOM
                    showSortSheet = false
                }
            )
            IosActionSheet(
                items = sortItems,
                onDismiss = { showSortSheet = false }
            )
        }

        // Add Item Dialog (Screenshot 4)
        if (showAddItemDialog) {
            AddItemDialog(
                accentColor = accentColor,
                isEditing = false,
                onDismiss = { showAddItemDialog = false },
                onConfirm = { name, detail, value, quantity ->
                    vm.addItem(name, value, quantity, detail)
                    showAddItemDialog = false
                }
            )
        }

        // Edit Item Dialog (Screenshot 4)
        itemToEdit?.let { target ->
            AddItemDialog(
                initialName = target.name,
                initialDetail = target.notes,
                initialValue = Formatters.compact(target.value),
                initialQuantity = Formatters.compact(target.quantity),
                accentColor = accentColor,
                isEditing = true,
                onDismiss = { itemToEdit = null },
                onConfirm = { name, detail, value, quantity ->
                    vm.updateItem(
                        target.copy(
                            name = name,
                            notes = detail,
                            value = value,
                            quantity = quantity
                        )
                    )
                    itemToEdit = null
                }
            )
        }

        // Long Press Context Menu for an item
        itemOptionsTarget?.let { target ->
            val options = listOf(
                ActionSheetItem("Edit Item...") {
                    itemOptionsTarget = null
                    itemToEdit = target
                },
                ActionSheetItem("Duplicate") {
                    itemOptionsTarget = null
                    vm.duplicateItem(target)
                },
                ActionSheetItem(if (target.completed) "Mark Pending" else "Mark Done") {
                    itemOptionsTarget = null
                    vm.setCompleted(target, !target.completed)
                },
                ActionSheetItem("Delete Item", isDestructive = true) {
                    itemOptionsTarget = null
                    vm.deleteItem(target)
                }
            )
            IosActionSheet(
                items = options,
                onDismiss = { itemOptionsTarget = null }
            )
        }

        // Confirm Delete List Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete List?", color = Color.White) },
                text = {
                    Text(
                        "Are you sure you want to delete \"${listName}\"? This action cannot be undone.",
                        color = textSecondary
                    )
                },
                containerColor = Color(0xFF1C1C1E),
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            vm.deleteCurrentList { onBack() }
                        }
                    ) {
                        Text("Delete", color = Color(0xFFFF453A), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}
