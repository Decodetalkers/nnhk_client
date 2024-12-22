package com.stein.nnhknews

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stein.nnhknews.nhk.NhkViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { NhkViewModel(InventoryApplication.container.newRepository) }
    }
}
