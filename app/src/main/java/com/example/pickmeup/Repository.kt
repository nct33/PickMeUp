package com.example.pickmeup

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

        fun setTags(tag : MutableList<String>) {
            instance.tags = tag
        }

        fun getTag(position : Int) : String {
            return instance.tags[position]
        }

        fun addTag(tag : String) {
            instance.tags.add(tag)
        }
    }
}