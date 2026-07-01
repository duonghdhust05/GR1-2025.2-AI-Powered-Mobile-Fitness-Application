package com.example.afit_gr1.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.afit_gr1.R

object ModalHelper {

    fun showGraphicModal(
        context: Context,
        title: String,
        subtitle: String,
        buttonText: String,
        onButtonClick: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_graphic_modal)
        
        // Transparent background is required for rounded corners to show properly
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleView = dialog.findViewById<TextView>(R.id.modal_title)
        val subtitleView = dialog.findViewById<TextView>(R.id.modal_subtitle)
        val btn = dialog.findViewById<Button>(R.id.modal_button)

        titleView.text = title
        subtitleView.text = subtitle
        btn.text = buttonText

        btn.setOnClickListener {
            onButtonClick()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showStandardModal(
        context: Context,
        title: String,
        subtitle: String,
        onConfirm: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_standard_modal)
        
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleView = dialog.findViewById<TextView>(R.id.modal_title)
        val subtitleView = dialog.findViewById<TextView>(R.id.modal_subtitle)
        val btnConfirm = dialog.findViewById<Button>(R.id.modal_btn_confirm)
        val btnCancel = dialog.findViewById<Button>(R.id.modal_btn_cancel)

        titleView.text = title
        subtitleView.text = subtitle

        btnConfirm.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
