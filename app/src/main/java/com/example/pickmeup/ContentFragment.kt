package com.example.pickmeup

import android.content.Intent
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
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

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
        myTagView.tags = CategoryRepository.getInstance()

        searchBar = contentView.findViewById(R.id.searchText)

        //remove selected tag
        myTagView.setOnTagClickListener(object : OnTagClickListener {
            override fun onTagClick(position: Int, text: String) {
                myTagView.removeTag(position)
                tagView.addTag(text)
                delete(UserRepository.getSession()!!.session_token, UserRepository.getSession()!!.id,
                    text)

                Repository.setTags(tagView.tags)
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
                add(UserRepository.getSession()!!.session_token, UserRepository.getSession()!!.id,
                    text)

                Repository.setTags(tagView.tags)
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

                for (i in Repository.getInstance().indices) {
                    if (Repository.getInstance()[i].contains(search))
                        list.add(Repository.getInstance()[i])
                }

                tagView.tags = list
            }
        })

        return contentView
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private val client = OkHttpClient()

    private fun add(authorization : String, userID : Int, category : String) {

        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"authorization": "$authorization", "category": "$category"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/$userID/category/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                    } else {
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val response = response.body!!.string()

                        println(response)

                        val moshi = Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(UserPref::class.java)

                        val categories = adapter.fromJson(response)

                        CategoryRepository.setTags(categories!!.data.getUserTags())
                    }
                }
            }
        }
    }

    private fun delete(authorization : String, userID : Int, category : String) {

        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"authorization": "$authorization", "category": "$category"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/$userID/category/")
                .delete(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                    } else {
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val response = response.body!!.string()

                        println(response)

                        val moshi = Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(UserPref::class.java)

                        val categories = adapter.fromJson(response)

                        CategoryRepository.setTags(categories!!.data.getUserTags())
                    }
                }
            }
        }
    }
}