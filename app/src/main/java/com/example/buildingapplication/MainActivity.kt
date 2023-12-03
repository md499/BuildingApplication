package com.example.buildingapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView
    private lateinit var save: Button
    private lateinit var retrieveButton: Button
    private val imageSize = 224

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage


    private val savedImageAndTextSet = HashSet<String>()
    private val savedImageHashes = HashSet<String>()

    private val storedImages = mutableListOf<String>()
    private lateinit var currentUserID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera = findViewById<Button>(R.id.button)
        gallery = findViewById<Button>(R.id.button2)

        result = findViewById(R.id.result)
        imageView = findViewById(R.id.imageView)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val username = intent.getStringExtra("USERNAME_EXTRA")
        val signOutButton = findViewById<Button>(R.id.buttonSignOut)

        val currentUser = firebaseAuth.currentUser
        currentUserID = currentUser?.uid ?: ""

        //Retrieve user's ID
        val saveButton = findViewById<Button>(R.id.saveImageButton)
        val retrieveButton = findViewById<Button>(R.id.retrieve)

        if (!username.isNullOrEmpty()) {
            welcomeTextView.text = "Welcome, $username"

            //Persistent storage
            val persistName = getSharedPreferences("user", MODE_PRIVATE)
            val editor = persistName.edit()
            editor.putString("Username", username)
            editor.apply()
        }


        camera.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }


        gallery.setOnClickListener {
            val cameraIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    // Gallery
                    val selectedImage: Uri? = data?.data
                    displayImage(selectedImage)
                }

                3 -> {
                    // Camera
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap
                    val uri = getImageUri(photo)
                    displayImage(uri)
                }
            }
        }
    }

    private fun displayImage(imageUri: Uri?) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val resizedBitmap = resizeBitmap(bitmap, imageSize, imageSize)
            imageView.setImageBitmap(resizedBitmap)
            processImage(resizedBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteBuffer.allocate(inImage.byteCount)
        inImage.copyPixelsToBuffer(bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
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

}


