package com.example.pickmeup

data class Feed(var type : String,
                var author : String,
                var category : String,
                var source : String)

data class ImageObtain(var data : MutableList<Feed>)
