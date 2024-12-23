package com.stein.nnhknews

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stein.nnhknews.nhk.NhkViewModel
import com.stein.nnhknews.settings.DessertReleaseViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { NhkViewModel(inventoryApplication().container.newRepository) }
        initializer { DessertReleaseViewModel(inventoryApplication().userPreferencesRepository) }
    }
}

fun CreationExtras.inventoryApplication(): InventoryApplication =
        (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)

