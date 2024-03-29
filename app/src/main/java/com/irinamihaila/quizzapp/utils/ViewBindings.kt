package com.irinamihaila.quizzapp.utils

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

public inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T,
): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater(layoutInflater)
    }