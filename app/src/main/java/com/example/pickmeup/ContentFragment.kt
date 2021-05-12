package com.example.pickmeup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView.OnTagClickListener


// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ContentFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tags : MutableList<String>
    private lateinit var myTagView : TagContainerLayout
    private lateinit var tagView : TagContainerLayout
    private lateinit var searchBar : EditText
    private var tagSearchArchive : MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        tags = Repository.getInstance()
        val contentView: View = inflater.inflate(R.layout.fragment_content, container, false)
        myTagView = contentView.findViewById(R.id.myTagView)
        tagView = contentView.findViewById(R.id.tagView)
        tagView.tags = tags
        tagSearchArchive = tags
        searchBar = contentView.findViewById(R.id.searchText)

        //remove selected tag
        myTagView.setOnTagClickListener(object : OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                myTagView.removeTag(position)
                tagView.addTag(text)

                tagSearchArchive = tagView.tags
            }

            override fun onTagLongClick(position: Int, text: String) {
            }

            override fun onSelectedTagDrag(position: Int, text: String) {
            }

            override fun onTagCrossClick(position: Int) {
            }
        })

        //add selected tag
        tagView.setOnTagClickListener(object : OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                tagView.removeTag(position)
                myTagView.addTag(text)



                tagSearchArchive = tagView.tags
            }

            override fun onTagLongClick(position: Int, text: String) {
            }

            override fun onSelectedTagDrag(position: Int, text: String) {
            }

            override fun onTagCrossClick(position: Int) {
            }
        })

        //setting up EditText View for searching up ListViews
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val search : String = searchBar.text.toString()
                val list : MutableList<String> = arrayListOf()

                for (i in tagSearchArchive.indices) {
                    if (tagSearchArchive[i].contains(search))
                        list.add(tagSearchArchive[i])
                }

                tagView.tags = list
            }
        })

        return contentView
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}