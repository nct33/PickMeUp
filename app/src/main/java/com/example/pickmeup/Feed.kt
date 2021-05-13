package com.example.pickmeup

data class Feed(var category : String,
                var source : String,
                var type : String,
                var author : String)

data class ObtainFeed(var data : MutableList<Feed>)
