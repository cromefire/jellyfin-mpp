package org.jellyfin.mpp.app.ui.crashlytics

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.jellyfin.mpp.app.R

enum class CrashlyticsResult {
    ENABLE, DISABLE, NEUTRAL
}

class CrashlyticsDialogFragment : DialogFragment() {
    private var listener: ((CrashlyticsResult) -> Unit)? = null

    fun setListener(listener: (CrashlyticsResult) -> Unit) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.enable_crashlytics)
                .setPositiveButton(R.string.enable) { _, _ ->
                    listener?.invoke(CrashlyticsResult.ENABLE)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    listener?.invoke(CrashlyticsResult.DISABLE)
                }
                .setOnDismissListener {
                    listener?.invoke(CrashlyticsResult.NEUTRAL)
                }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }
}
