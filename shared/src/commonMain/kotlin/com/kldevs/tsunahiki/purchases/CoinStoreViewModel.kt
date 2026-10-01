package com.kldevs.tsunahiki.purchases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.launch

class CoinStoreViewModel : ViewModel() {
    var offering by mutableStateOf<Offering?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadOfferings()
    }

    private fun loadOfferings() {
        viewModelScope.launch {
            try {
                val offerings = Purchases.sharedInstance.awaitOfferings()
                offering = offerings.current
            } catch (e: Exception) {
                errorMessage = "Failed to load products: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun purchase(packageToBuy: Package, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val result = Purchases.sharedInstance.awaitPurchase(packageToBuy)

                // Map product identifier to coin amount
                val coins = when (packageToBuy.identifier) {
                    "coins_100" -> 100
                    "coins_500" -> 500
                    "coins_1200" -> 1200
                    "coins_2500" -> 2500
                    "starter_pack" -> 1000
                    else -> 0
                }
                onSuccess(coins)
            } catch (e: Exception) {
                errorMessage = "Purchase failed: ${e.message}"
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}