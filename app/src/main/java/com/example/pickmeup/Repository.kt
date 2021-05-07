package com.example.pickmeup

//I was thinking of making a repository to make it easier for me to use the search function I'm
//Implementing, but I'm not really sure how firebase works anyways so I'll just leave this like so.
class Repository private constructor(private var tags: MutableList<String>) {

    fun getList() : MutableList<String> {
        return instance.tags
    }

    companion object {
        private val instance = Repository(arrayListOf())

        fun getInstance() : MutableList<String> {
            return instance.getList()
        }

        fun setTag(tag : String, position : Int) {
            instance.tags[position] = tag
        }

        fun getTag(position : Int) : String {
            return instance.tags[position]
        }
    }
}