package com.example.pickmeup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var contentFeed : RecyclerView
private var dummyList : MutableList<Feed> = arrayListOf(
    Feed("content", R.drawable.ic_menu_camera, arrayListOf("this", "that", "these"),
        "https://google.com"),
    Feed("I have come to accept the feeling of not knowing where I am going. " +
            "And I have trained myself to love it. Because it is only when we " +
            "are suspended in mid-air with no landing in sight, that we force our " +
            "wings to unravel and alas begin our flight. And as we fly, we still " +
            "may not know where we are going to. But the miracle is in the unfolding " +
            "of the wings. You may not know where you're going, but you know that " +
            "so long as you spread your wings, the winds will carry you.",
    R.drawable.goodread_testconcept,
    arrayListOf("belief", "direction", "faith", "flight", "flying", "flying-spirit", "hope",
            "inspirational", "inspirational-life", "inspirational-quotes", "life", "life-and-living",
            "living", "trust", "uplifting", "winds", "winds-of-life", "wings"),
    "https://www.goodreads.com/quotes/tag/uplifting")
)

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        val adapter = Adapter(dummyList, this)
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
        //Probably something along the lines of go to browser with source url.
        val source = dummyList[position].source
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(source)
        startActivity(intent)
    } //changes
}