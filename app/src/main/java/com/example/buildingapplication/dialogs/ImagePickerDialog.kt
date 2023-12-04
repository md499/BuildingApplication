package com.example.buildingapplication.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.buildingapplication.R


class ImagePickerDialog(
    private val onTakePictureClick: () -> Unit,
    private val onPickPhotoClick: () -> Unit
) : DialogFragment() {
    
    private lateinit var pickPhotoTv: TextView
    private lateinit var takePhotoTv: TextView
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        
        val view: View = inflater.inflate(R.layout.dialog_image_picker, container, false)
        
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        pickPhotoTv = view.findViewById(R.id.pick_photo)
        takePhotoTv = view.findViewById(R.id.take_photo)
        
        takePhotoTv.setOnClickListener {
            onTakePictureClick()
            dismiss()
        }
        
        pickPhotoTv.setOnClickListener {
            onPickPhotoClick()
            dismiss()
        }
        
        return view
    }
    
}