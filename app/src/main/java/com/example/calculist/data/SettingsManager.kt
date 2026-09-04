package com.example.calculist.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calculist_prefs", Context.MODE_PRIVATE)

    private val _currencySymbol = MutableStateFlow(prefs.getString(KEY_CURRENCY, "") ?: "")
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _decimalFormat = MutableStateFlow(prefs.getString(KEY_DECIMAL, "AUTO") ?: "AUTO")
    val decimalFormat: StateFlow<String> = _decimalFormat

    private val _textSize = MutableStateFlow(prefs.getString(KEY_TEXT_SIZE, "DEFAULT") ?: "DEFAULT")
    val textSize: StateFlow<String> = _textSize

    fun setCurrency(symbol: String) {
        prefs.edit().putString(KEY_CURRENCY, symbol).apply()
        _currencySymbol.value = symbol
    }

    fun setDecimalFormat(format: String) {
        prefs.edit().putString(KEY_DECIMAL, format).apply()
        _decimalFormat.value = format
    }

    fun setTextSize(size: String) {
        prefs.edit().putString(KEY_TEXT_SIZE, size).apply()
        _textSize.value = size
    }

    companion object {
        private const val KEY_CURRENCY = "pref_currency"
        private const val KEY_DECIMAL = "pref_decimal"
        private const val KEY_TEXT_SIZE = "pref_text_size"

        @Volatile
        private var instance: SettingsManager? = null

        fun get(context: Context): SettingsManager =
            instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
    }
}
