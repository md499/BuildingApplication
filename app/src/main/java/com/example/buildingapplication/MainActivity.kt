package com.example.buildingapplication

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.buildingapplication.dialogs.ImagePickerDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    
    private lateinit var imagePicker1: CardView
    private lateinit var imagePicker2: CardView
    private lateinit var imagePicker3: CardView
    private lateinit var imagePicker4: CardView
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    
    private var currentPickingImage: ImageView? = null
    
    private lateinit var photoPicker: ActivityResultLauncher<PickVisualMediaRequest>
    
    private var takePictureUri: Uri? = null
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    
    private val savedImageAndTextSet = HashSet<String>()
    private val savedImageHashes = HashSet<String>()
    
    private val storedImages = mutableListOf<String>()
    private lateinit var currentUserID: String
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseStorage = FirebaseStorage.getInstance()
        
        photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                getBitmapFromUri(uri)
                    ?.let { bitmap ->
                        currentPickingImage?.apply {
                            setImageBitmap(bitmap)
                        }
                    }
            }
            currentPickingImage = null
        }
        
        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess){
                takePictureUri?.let {
                    getBitmapFromUri(it)
                        ?.let { bitmap ->
                            currentPickingImage?.apply {
                                setImageBitmap(bitmap)
                            }
                        }
                }
            }
            currentPickingImage = null
        }
        
        imagePicker1 = findViewById(R.id.image_picker1)
        imagePicker2 = findViewById(R.id.image_picker2)
        imagePicker3 = findViewById(R.id.image_picker3)
        imagePicker4 = findViewById(R.id.image_picker4)
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        image3 = findViewById(R.id.image3)
        image4 = findViewById(R.id.image4)
        
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val username = intent.getStringExtra("USERNAME_EXTRA")
        val signOutButton = findViewById<TextView>(R.id.buttonSignOut)

//        val currentUser = firebaseAuth.currentUser
//        currentUserID = currentUser?.uid ?: ""

//        if (!username.isNullOrEmpty()) {
//            welcomeTextView.text = "Welcome, $username"
//
//            //Persistent storage
//            val persistName = getSharedPreferences("user", MODE_PRIVATE)
//            val editor = persistName.edit()
//            editor.putString("Username", username)
//            editor.apply()
//        }
        
        imagePicker1.setOnClickListener { pickOrTakePicture(image1) }
        imagePicker2.setOnClickListener { pickOrTakePicture(image2) }
        imagePicker3.setOnClickListener { pickOrTakePicture(image3) }
        imagePicker4.setOnClickListener { pickOrTakePicture(image4) }
        
        signOutButton.setOnClickListener { }
        
    }
    
    private fun pickOrTakePicture(currentImageView: ImageView) {
        val imagePickerDialog = ImagePickerDialog(
            onTakePictureClick = {
                createImageUri()
                currentPickingImage = currentImageView
                takePicture.launch(takePictureUri)
            },
            onPickPhotoClick = {
                currentPickingImage = currentImageView
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )
        imagePickerDialog.show(supportFragmentManager, imagePickerDialog.tag)
    }
    
    private fun processImage(bitmap: Bitmap) {
        TODO("Implement")
    }
    
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer =
            ByteBuffer.allocateDirect(4 * 224 * 224 * 3) // Assuming FLOAT32, adjust if needed
        byteBuffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(224 * 224)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        for (pixelValue in pixels) {
            byteBuffer.putFloat((pixelValue shr 16 and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixelValue shr 8 and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
        }
        
        return byteBuffer
    }
    
    private fun getBitmapFromUri(imageUri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createImageUri() {
        val file = File(applicationContext.filesDir, "camera_photo.png")
        takePictureUri = FileProvider.getUriForFile(applicationContext, "com.example.buildingapplication.fileProvider", file)
    }
    
}


