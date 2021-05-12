package com.example.pickmeup

import java.util.*

class Category(var category : String)

class Tags(var data: MutableList<Category>) {
    fun getTags() : MutableList<String> {
        val tags : MutableList<String> = arrayListOf()
        for(i in data.indices) {
            tags += data[i].category
        }
        return tags
    }
}

data class UserCategory(var categories : MutableList<Category>) {
    fun getUserTags() : MutableList<String> {
        val tags : MutableList<String> = arrayListOf()
        for(i in categories.indices) {
            tags += categories[i].category
        }
        return tags
    }
}
data class UserPref(var data : UserCategory)