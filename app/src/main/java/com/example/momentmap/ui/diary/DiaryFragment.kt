package com.example.momentmap.ui.diary

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.momentmap.ui.moments.MyAdapter
import com.example.momentmap.R
import com.example.momentmap.data.Moment
import com.example.momentmap.databinding.FragmentDiaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class DiaryFragment : Fragment() {

    private lateinit var binding: FragmentDiaryBinding
    private lateinit var navController: NavController
    private lateinit var momentList: ArrayList<Moment>
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MyAdapter
    private lateinit var database: DatabaseReference
    private lateinit var eventListener: ValueEventListener

    private val formatterWithoutTime: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    var dateToShow: String = LocalDate.now().format(formatterWithoutTime).toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDiaryBinding.inflate(inflater, container, false)
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


       eventListener= database!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                momentList.clear()
                for(itemSnapshot in snapshot.children){
                    var moment = itemSnapshot.getValue(Moment::class.java)
                    moment!!.id = itemSnapshot.key;
                    if(moment!=null && moment.date == dateToShow){
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

        val calendar = binding.calendarView

        calendar.setOnDateChangeListener { calendarView, i, i2, i3 ->
            var month = (i2+1).toString()
            var day = (i3).toString()

            if((i2 + 1) < 10){
                month = "0$month"
            }
            if(i3 < 10){
                day = "0$day"
            }
            val dateSelected = "$day/$month/$i"
            calendarClicked(dateSelected)


        }

        return binding.root
    }

    private fun calendarClicked(dateSelected: String) {

        eventListener= database!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                momentList.clear()
                for(itemSnapshot in snapshot.children){
                    var moment = itemSnapshot.getValue(Moment::class.java)
                    moment!!.id = itemSnapshot.key;
                    if(moment!=null && moment.date == dateSelected){
                        momentList.add(moment)
                    }
                }
                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

}