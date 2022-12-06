package com.example.newsapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var imageUri: Uri?= null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
        binding.editBtn.setOnClickListener {
            showImageAttachMenu()
        }
        binding.okBtn.setOnClickListener{
            validateData()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateData() {
        if (imageUri!=null){
            uploadImage()
        }
    }
    private fun uploadImage(){
        progressDialog.setMessage("Uploading profile image")
        progressDialog.show()
        val filPathAndName = "ProfileImages/"+firebaseAuth.uid
        val reference = FirebaseStorage.getInstance().getReference(filPathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener{ taskSnapshot->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"
                updateProfile(uploadedImageUrl)
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload image due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Updating profile...")
        val hashMap:HashMap<String, Any> = HashMap()

        if (imageUri != null){
            hashMap["profileImage"] = uploadedImageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to update profile due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    binding.nameTv.text = name
//                    binding.emailTv.text = email

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.cr)
                            .into(binding.profileIv)
                    }
                    catch (e: Exception){

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            loadUserInfo()
        }
        else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showImageAttachMenu(){
        val popMenu = PopupMenu(this, binding.profileIv)
//        popMenu.menu.add(Menu.NONE,0,0,"Camera")
        popMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popMenu.show()

        popMenu.setOnMenuItemClickListener { item->
            val id = item.itemId
//            if (id == 0){
//                pickImageCamera()
//            }else
                if (id == 1){
                pickImageGallery()
            }

            true
        }
    }

//    private fun pickImageCamera() {
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
//        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Description")
//
//        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
//
//        cameraActivityResultLauncher.launch(intent)
//
//
//    }
//    private val cameraActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult(),
//        ActivityResultCallback<ActivityResult> {result ->
//            if (result.resultCode == Activity.RESULT_OK){
//                val data = result.data
//                //imageUri = data!!.data
//
//                binding.profileIv.setImageURI(imageUri)
//            }
//            else{
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    )

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.profileIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

}