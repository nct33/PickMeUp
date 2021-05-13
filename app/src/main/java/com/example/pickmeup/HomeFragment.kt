package com.example.pickmeup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var contentFeed : RecyclerView

class HomeFragment : Fragment(), Adapter.SetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val homeView: View = inflater.inflate(R.layout.fragment_home, container, false)

        contentFeed = homeView.findViewById(R.id.content_feed)

        val adapter = Adapter(FeedRepository.getInstance(), this)
        contentFeed.adapter = adapter
        contentFeed.layoutManager = LinearLayoutManager(requireContext())

        return homeView
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onClick(position: Int) {
        if(FeedRepository.getInstance()[position].type == "photo") {
            val source = FeedRepository.getInstance()[position].source
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(source)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No source linked to content.",
                Toast.LENGTH_SHORT).show()
        }
    }
}