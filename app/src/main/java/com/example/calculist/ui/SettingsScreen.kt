package com.example.calculist.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calculist.data.SettingsManager

@Composable
fun SettingsScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val settings = remember { SettingsManager.get(context) }
    val currentCurrency by settings.currencySymbol.collectAsState()
    val currentDecimal by settings.decimalFormat.collectAsState()

    var showNumberFormattingDialog by remember { mutableStateOf(false) }
    var showTextSettingsDialog by remember { mutableStateOf(false) }
    var showAppIconDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showHowToDialog by remember { mutableStateOf(false) }
    var showFeatureDialog by remember { mutableStateOf(false) }
    var showBugDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val darkBg = Color(0xFF000000)
    val cardBg = Color(0xFF1C1C1E)
    val orangeAccent = Color(0xFFFF9F0A)
    val dividerColor = Color(0xFF2C2C2E)
    val textSecondary = Color(0xFF8E8E93)

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.size(44.dp))

                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Orange circular checkmark button (Screenshot 1)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(orangeAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Group 1
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(cardBg)
                    ) {
                        SettingsRow(
                            leadingBadge = {
                                Text(
                                    "123",
                                    color = orangeAccent,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            title = "Default Number Formatting",
                            value = if (currentCurrency.isBlank()) "Standard" else currentCurrency,
                            onClick = { showNumberFormattingDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingBadge = {
                                Text(
                                    "Aa",
                                    color = orangeAccent,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            title = "Text Settings",
                            onClick = { showTextSettingsDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingIcon = Icons.Default.CropSquare,
                            leadingIconColor = orangeAccent,
                            title = "App Icon",
                            onClick = { showAppIconDialog = true }
                        )
                    }
                }

                // Group 2
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(cardBg)
                    ) {
                        SettingsRow(
                            leadingIcon = Icons.Default.Share,
                            leadingIconColor = orangeAccent,
                            title = "Share",
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Calculate your lists with CalcuList!")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share CalcuList"))
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingIcon = Icons.Default.ThumbUp,
                            leadingIconColor = orangeAccent,
                            title = "Rate",
                            onClick = { showRateDialog = true }
                        )
                    }
                }

                // Group 3
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(cardBg)
                    ) {
                        SettingsRow(
                            leadingIcon = Icons.Default.HelpOutline,
                            leadingIconColor = orangeAccent,
                            title = "How-To",
                            onClick = { showHowToDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingIcon = Icons.Default.ChatBubbleOutline,
                            leadingIconColor = orangeAccent,
                            title = "Request a Feature",
                            onClick = { showFeatureDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingIcon = Icons.Default.BugReport,
                            leadingIconColor = orangeAccent,
                            title = "Report a Bug",
                            onClick = { showBugDialog = true }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 52.dp),
                            color = dividerColor,
                            thickness = 0.5.dp
                        )
                        SettingsRow(
                            leadingIcon = Icons.Default.Info,
                            leadingIconColor = orangeAccent,
                            title = "About",
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Number Formatting Sheet/Dialog
    if (showNumberFormattingDialog) {
        Dialog(onDismissRequest = { showNumberFormattingDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Default Number Formatting",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Currency Symbol", color = textSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val currencies = listOf(
                        "" to "None (e.g. 707)",
                        "₹" to "₹ (Indian Rupee)",
                        "$" to "$ (USD)",
                        "€" to "€ (Euro)",
                        "£" to "£ (GBP)"
                    )
                    currencies.forEach { (sym, label) ->
                        val isSelected = currentCurrency == sym
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF2C2C2E) else Color.Transparent)
                                .clickable { settings.setCurrency(sym) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = Color.White, fontSize = 15.sp)
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = orangeAccent)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showNumberFormattingDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Text Settings Dialog
    if (showTextSettingsDialog) {
        Dialog(onDismissRequest = { showTextSettingsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Text Settings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "CalcuList uses system typography with dynamic scaling for optimal readability across all screens.",
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showTextSettingsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // App Icon Dialog
    if (showAppIconDialog) {
        Dialog(onDismissRequest = { showAppIconDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("App Icon", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CL", color = orangeAccent, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("CalcuList Default Icon", color = Color.White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAppIconDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Rate Dialog
    if (showRateDialog) {
        var rating by remember { mutableIntStateOf(5) }
        Dialog(onDismissRequest = { showRateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enjoying CalcuList?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap a star to rate", color = textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$i Stars",
                                tint = if (i <= rating) orangeAccent else Color(0xFF3A3A3C),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { rating = i }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Thank you for your rating!", Toast.LENGTH_SHORT).show()
                            showRateDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Submit Rating", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // How-To Dialog
    if (showHowToDialog) {
        Dialog(onDismissRequest = { showHowToDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("How-To CalcuList", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    val instructions = listOf(
                        "1. Create Lists" to "Tap + Add List on the home screen. Pick a color and icon to customize your list.",
                        "2. Add Items" to "Tap + Add Item inside any list. Enter the item name, optional detail, unit value, and quantity.",
                        "3. Auto Calculations" to "CalcuList multiplies value × quantity and displays real-time remaining and completed totals.",
                        "4. Check Off Items" to "Tap any item to mark it complete. The total remaining adjusts automatically.",
                        "5. Sorting & Options" to "Tap the Sort button or the more menu (...) to sort, reset, or customize formatting."
                    )
                    instructions.forEach { (heading, desc) ->
                        Text(heading, color = orangeAccent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(desc, color = textSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showHowToDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Got it", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Feature / Bug Dialogs
    if (showFeatureDialog || showBugDialog) {
        val isBug = showBugDialog
        var text by remember { mutableStateOf("") }
        Dialog(onDismissRequest = {
            showFeatureDialog = false
            showBugDialog = false
        }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        if (isBug) "Report a Bug" else "Request a Feature",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (isBug) "Describe what went wrong:" else "What feature would you love to see?",
                        color = textSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeAccent,
                            unfocusedBorderColor = Color(0xFF3A3A3C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                if (isBug) "Bug report received. Thank you!" else "Feature request submitted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            showFeatureDialog = false
                            showBugDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Send", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // About Dialog
    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("About CalcuList", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Version 1.0.0", color = textSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "CalcuList combines lists with powerful calculation capabilities. Everything stays 100% on your device, private and offline.",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAboutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = orangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    value: String? = null,
    leadingIcon: ImageVector? = null,
    leadingIconColor: Color = Color(0xFFFF9F0A),
    leadingBadge: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (leadingBadge != null) {
                    leadingBadge()
                } else if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = leadingIconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(
                    text = value,
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF48484A),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
