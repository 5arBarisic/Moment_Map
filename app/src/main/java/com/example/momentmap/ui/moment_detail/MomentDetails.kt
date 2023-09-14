package com.example.momentmap.ui.moment_detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.momentmap.MainActivity
import com.example.momentmap.R
import com.example.momentmap.databinding.ActivityMomentDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar


class MomentDetails : AppCompatActivity() {
    private var imageUrl = ""
    private lateinit var binding: ActivityMomentDetailsBinding
    private var uri: Uri? = null
    private var imageURL: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var id: String? = null
    private val moment = mutableMapOf<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMomentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val activityResultLauncherForUploadImage =
            registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    uri = data!!.data
                    binding.detailImage.setImageURI(uri)
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                }
            }
        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("Id")
            binding.detailDesc.setText( bundle.getString("Description"))
            binding.detailTitle.setText (bundle.getString("Title"))
            binding.detailLocation.setText(bundle.getString("Location"))
            binding.detailDate.setText(bundle.getString("Date"))
            if (bundle.getString("Image") != null && bundle.getString("Image") != "") {
                imageUrl = bundle.getString("Image")!!
                Glide.with(this).load(bundle.getString("Image"))
                    .into(binding.detailImage)
            }
        }
        binding.detailTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moment["title"] = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.detailDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moment["description"] = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.detailLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moment["location"] = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.detailDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moment["date"] = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.detailImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncherForUploadImage.launch(photoPicker)
        }

        binding.detailDate.setOnClickListener { onDateClick() }
        binding.updateButton.setOnClickListener {
            saveData(id.toString(),this)
        }
        binding.deleteButton.setOnClickListener {
            deleteData(id.toString())
        }
    }

    private fun deleteData(childId: String) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase
            .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        database.child(auth.currentUser?.uid.toString()).child("Moments")
            .child(childId).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Moment deleted!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Moment isn't deleted!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun onDateClick() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                var m = (monthOfYear + 1).toString()
                var d = dayOfMonth.toString()
                if ((monthOfYear + 1) < 10) {
                    m = "0$m"
                }
                if (dayOfMonth < 10) {
                    d = "0$d"
                }
                val dat = ("$d/$m/$year")
                binding.detailDate.setText(dat)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveData(childId: String,context: Context) {
        if(uri != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("Moment images")
                .child(uri!!.lastPathSegment!!)


            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setView(R.layout.progress)
            val dialog = builder.create()
            dialog.show()
            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->

                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageURL = urlImage.toString()
                dialog.dismiss()
                uploadData(childId)



            }.addOnFailureListener {
                dialog.dismiss()
            }
        }else{
            uploadData(childId)
        }

    }

    private fun uploadData(childId: String) {

        if(imageURL != null){
            moment["imageUrl"] = imageURL.toString()
        }else{
            moment["imageUrl"] = imageUrl
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase
            .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        database.child(auth.currentUser?.uid.toString()).child("Moments")
            .child(childId).updateChildren(moment as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(this, "Moment updated!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Moment isn't updated!", Toast.LENGTH_SHORT).show()
            }

    }
}