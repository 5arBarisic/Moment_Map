package com.example.momentmap

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.momentmap.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var momentList: ArrayList<Moment>
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyAdapter
    private lateinit var database: DatabaseReference
    private lateinit var eventListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        navController = NavHostFragment.findNavController(this)

        val gridLayoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this.context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()
        dialog.show()

        momentList = ArrayList()
        adapter = MyAdapter(this.requireContext(),momentList)
        binding.recyclerView.adapter  =adapter
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase
            .getInstance("https://momentmap-faef6-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("Moments")

        dialog.show()

        eventListener = database!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                momentList.clear()
                for(itemSnapshot in snapshot.children){
                    val moment = itemSnapshot.getValue(Moment::class.java)
                    if(moment!=null){
                        momentList.add(moment)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_addMomentFragment)
        }

        return binding.root
    }
}