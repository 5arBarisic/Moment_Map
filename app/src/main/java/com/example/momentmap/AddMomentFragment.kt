package com.example.momentmap

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.momentmap.databinding.FragmentAddMomentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID

class AddMomentFragment : Fragment() {

    private lateinit var binding: FragmentAddMomentBinding
    private var imageURL: String? = null
    private var uri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMomentBinding.inflate(layoutInflater)
        val rootView = binding.root

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data
                binding.uploadImage.setImageURI(uri)
            } else {
                Toast.makeText(this.context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        binding.uploadDate.setOnClickListener { onDateClick() }

        binding.saveButton.setOnClickListener {

            saveData()
        }

        return rootView
    }

    private fun onDateClick(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
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
                binding.uploadDate.setText(dat)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveData() {
        val storageReference = FirebaseStorage.getInstance().reference.child("Moment images")
            .child(uri!!.lastPathSegment!!)


        val builder = AlertDialog.Builder(this.activity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while(!uriTask.isComplete);
                val urlImage = uriTask.result
            imageURL = urlImage.toString()
            uploadData()
            dialog.dismiss()


        }.addOnFailureListener{
            dialog.dismiss()
        }

    }

    private fun uploadData(){
        val title = binding.uploadTitle.text.toString()
        val desc = binding.uploadDesc.text.toString()
        val location = binding.uploadLocation.text.toString()
        val date = binding.uploadDate.text.toString()

        val moment = Moment(title,desc,location,date,imageURL)

        navController = NavHostFragment.findNavController(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase
            .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        database.child(auth.currentUser?.uid.toString()).child("Moments")
            .child(UUID.randomUUID().toString()).setValue(moment).addOnSuccessListener {
                Toast.makeText(this.context, "Moment saved!", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_addMomentFragment_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(this.context, "Moment isn't saved!", Toast.LENGTH_SHORT).show()
            }

    }
}