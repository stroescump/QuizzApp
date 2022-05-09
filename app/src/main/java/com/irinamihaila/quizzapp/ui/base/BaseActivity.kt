package com.irinamihaila.quizzapp.ui.base

import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.utils.NetworkWatcher

abstract class BaseActivity : AppCompatActivity() {
    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }
    private val networkWatcher by lazy { NetworkWatcher(connectivityManager) }
    abstract val binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.also {
            setContentView(it.root)
        }
    }


    private fun AlertDialog.Builder.setButton(
        buttonHandlerType: AlertDialogButton,
        buttonHandler: ((AlertDialog) -> DialogInterface.OnClickListener)?
    ) {
        if (buttonHandler.notNull()) {
            when (buttonHandlerType) {
                is AlertDialogButton.PositiveButton -> setPositiveButton(
                    getString(R.string.ok_button),
                    buttonHandler!!(this.create())
                )
                is AlertDialogButton.NegativeButton -> setNegativeButton(
                    getString(R.string.dismiss),
                    buttonHandler!!(this.create())
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkWatcher.distinctUntilChanged().observe(this) {
            handleNetworkWatcher(it)
        }
    }

    private fun handleNetworkWatcher(isNetworkAvailable: Boolean) {
        //TODO - create base behavior for lack of network as app is online experience mostly
    }

    override fun onPause() {
        super.onPause()
        networkWatcher.removeObservers(this)
    }

    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun ((AlertDialog) -> DialogInterface.OnClickListener)?.notNull(): Boolean {
        return this != null
    }

    sealed class AlertDialogButton {
        object PositiveButton : AlertDialogButton()
        object NegativeButton : AlertDialogButton()
    }

}