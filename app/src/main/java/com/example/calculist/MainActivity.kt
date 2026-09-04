package com.example.calculist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calculist.ui.CalculatorScreen
import com.example.calculist.ui.ItemEditScreen
import com.example.calculist.ui.ListsScreen
import com.example.calculist.ui.SettingsScreen
import com.example.calculist.ui.theme.CalcuListTheme

/**
 * Creates a ViewModel from the app container, keyed so each screen instance
 * gets its own ViewModel.
 */
@Composable
inline fun <reified VM : ViewModel> calcViewModel(
    key: String? = null,
    crossinline create: (CalcuListApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as CalcuListApp
    return viewModel(
        key = key,
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = create(app) as T
        }
    )
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            CalcuListTheme(darkTheme = true) {
                CalcuListRoot()
            }
        }
    }
}

@Composable
fun CalcuListRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lists") {

        composable("lists") {
            ListsScreen(
                onOpenList = { listId -> navController.navigate("list/$listId") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                onDismiss = { navController.popBackStack() }
            )
        }

        composable(
            route = "list/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) { entry ->
            val listId = entry.arguments?.getLong("listId") ?: 0L
            CalculatorScreen(
                listId = listId,
                onBack = { navController.popBackStack() },
                onAddItem = { navController.navigate("item/$listId") },
                onEditItem = { itemId -> navController.navigate("item/$listId?itemId=$itemId") }
            )
        }

        composable(
            route = "item/{listId}?itemId={itemId}",
            arguments = listOf(
                navArgument("listId") { type = NavType.LongType },
                navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { entry ->
            val listId = entry.arguments?.getLong("listId") ?: 0L
            val itemId = entry.arguments?.getLong("itemId") ?: -1L
            ItemEditScreen(
                listId = listId,
                itemId = itemId,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
