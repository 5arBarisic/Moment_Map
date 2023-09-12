package com.example.momentmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.momentmap.databinding.ActivityMomentDetailsBinding

class MomentDetails : AppCompatActivity() {
    private var imageUrl = ""
    private lateinit var binding: ActivityMomentDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMomentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent.extras
        if (bundle != null) {
            binding.detailDesc.text = bundle.getString("Description")
            binding.detailTitle.text = bundle.getString("Title")
            binding.detailLocation.setText(bundle.getString("Location"))
            binding.detailDate.setText(bundle.getString("Date"))
            if(bundle.getString("Image") != null && bundle.getString("Image") !="" ) {
                imageUrl = bundle.getString("Image")!!
                Glide.with(this).load(bundle.getString("Image"))
                    .into(binding.detailImage)
            }
        }
    }
}