package com.stein.nnhknews

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stein.nnhknews.nhk.NhKViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { NhKViewModel(InventoryApplication.container.newRepository) }
    }
}
