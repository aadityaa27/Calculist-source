package com.example.calculist.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ListVisualColor(
    val hex: String,
    val color: Color
)

data class ListVisualIcon(
    val id: String,
    val icon: ImageVector
)

object ListVisuals {

    val palette: List<ListVisualColor> = listOf(
        ListVisualColor("#FF453A", Color(0xFFFF453A)), // Red / Coral
        ListVisualColor("#FF9F0A", Color(0xFFFF9F0A)), // Orange
        ListVisualColor("#FFD60A", Color(0xFFFFD60A)), // Yellow
        ListVisualColor("#30D158", Color(0xFF30D158)), // Green
        ListVisualColor("#64D2FF", Color(0xFF64D2FF)), // Cyan / Mint
        ListVisualColor("#0A84FF", Color(0xFF0A84FF)), // Blue
        ListVisualColor("#5E5CE6", Color(0xFF5E5CE6)), // Indigo
        ListVisualColor("#BF5AF2", Color(0xFFBF5AF2)), // Purple
        ListVisualColor("#FF375F", Color(0xFFFF375F)), // Pink
        ListVisualColor("#8E8E93", Color(0xFF8E8E93)), // Gray
        ListVisualColor("#48484A", Color(0xFF48484A)), // Dark Gray
        ListVisualColor("#A2845E", Color(0xFFA2845E))  // Brown
    )

    val icons: List<ListVisualIcon> = listOf(
        ListVisualIcon("list", Icons.Default.FormatListBulleted),
        ListVisualIcon("grid", Icons.Default.GridView),
        ListVisualIcon("check", Icons.Default.Check),
        ListVisualIcon("flag", Icons.Default.Flag),
        ListVisualIcon("bookmark", Icons.Default.Bookmark),
        ListVisualIcon("ribbon", Icons.Default.EmojiEvents),

        ListVisualIcon("document", Icons.Default.Description),
        ListVisualIcon("folder", Icons.Default.Folder),
        ListVisualIcon("box", Icons.Default.Inventory2),
        ListVisualIcon("cube", Icons.Default.Category),
        ListVisualIcon("briefcase", Icons.Default.Work),
        ListVisualIcon("printer", Icons.Default.Print),

        ListVisualIcon("pencil", Icons.Default.Edit),
        ListVisualIcon("scissors", Icons.Default.ContentCut),
        ListVisualIcon("paperclip", Icons.Default.AttachFile),
        ListVisualIcon("pin", Icons.Default.PushPin),
        ListVisualIcon("mail", Icons.Default.Email),
        ListVisualIcon("chair", Icons.Default.Weekend),

        ListVisualIcon("calendar", Icons.Default.CalendarToday),
        ListVisualIcon("alarm", Icons.Default.AccessAlarm),
        ListVisualIcon("timer", Icons.Default.Timer),
        ListVisualIcon("hourglass", Icons.Default.HourglassTop),
        ListVisualIcon("card", Icons.Default.CreditCard),
        ListVisualIcon("cart", Icons.Default.ShoppingCart),

        ListVisualIcon("tag", Icons.Default.LocalOffer),
        ListVisualIcon("bag", Icons.Default.ShoppingBag),
        ListVisualIcon("car", Icons.Default.DirectionsCar),
        ListVisualIcon("train", Icons.Default.DirectionsTransit),
        ListVisualIcon("airplane", Icons.Default.Flight),
        ListVisualIcon("send", Icons.Default.Send),

        ListVisualIcon("place", Icons.Default.Place),
        ListVisualIcon("map", Icons.Default.Map),
        ListVisualIcon("battery", Icons.Default.BatteryChargingFull),
        ListVisualIcon("computer", Icons.Default.Computer),
        ListVisualIcon("tv", Icons.Default.Tv),
        ListVisualIcon("movie", Icons.Default.Movie)
    )

    fun colorFromHex(hex: String): Color {
        return palette.firstOrNull { it.hex.equals(hex, ignoreCase = true) }?.color
            ?: try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (e: Exception) {
                Color(0xFFFF453A)
            }
    }

    fun iconFromId(id: String): ImageVector {
        return icons.firstOrNull { it.id == id }?.icon ?: Icons.Default.FormatListBulleted
    }
}
