package com.example.calculist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun NewListDialog(
    initialName: String = "",
    initialColorHex: String = "#FF453A",
    initialIconId: String = "list",
    title: String = "New List",
    onDismiss: () -> Unit,
    onSave: (name: String, colorHex: String, iconId: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColorHex by remember { mutableStateOf(initialColorHex) }
    var selectedIconId by remember { mutableStateOf(initialIconId) }

    val focusManager = LocalFocusManager.current
    val darkBg = Color(0xFF121214)
    val inputBg = Color(0xFF1C1C1E)
    val buttonBg = Color(0xFF2C2C2E)
    val currentColor = ListVisuals.colorFromHex(selectedColorHex)
    val currentIcon = ListVisuals.iconFromId(selectedIconId)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = darkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar (Screenshot 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close 'X' button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(buttonBg)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Done '✓' button
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), selectedColorHex, selectedIconId)
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (name.isNotBlank()) buttonBg else Color(0xFF1C1C1E))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = if (name.isNotBlank()) Color.White else Color(0xFF48484A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Giant Circular Icon Preview (Screenshot 3)
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(currentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("List Name", color = Color(0xFF8E8E93)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        disabledContainerColor = inputBg,
                        focusedBorderColor = Color(0xFF3A3A3C),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Color Palette Picker: 2 rows of 6 circles
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val row1 = ListVisuals.palette.take(6)
                    val row2 = ListVisuals.palette.drop(6).take(6)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row1.forEach { item ->
                            val isSelected = selectedColorHex.equals(item.hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .border(2.dp, Color(0xFF8E8E93), CircleShape)
                                                .padding(3.dp)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clip(CircleShape)
                                    .background(item.color)
                                    .clickable { selectedColorHex = item.hex }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row2.forEach { item ->
                            val isSelected = selectedColorHex.equals(item.hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .border(2.dp, Color(0xFF8E8E93), CircleShape)
                                                .padding(3.dp)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clip(CircleShape)
                                    .background(item.color)
                                    .clickable { selectedColorHex = item.hex }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Icon Picker: 6 columns grid of 36 icons
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ListVisuals.icons) { visualIcon ->
                        val isSelected = selectedIconId == visualIcon.id
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .border(2.dp, Color.White, CircleShape)
                                            .padding(2.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clip(CircleShape)
                                .background(buttonBg)
                                .clickable { selectedIconId = visualIcon.id },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = visualIcon.icon,
                                contentDescription = visualIcon.id,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
