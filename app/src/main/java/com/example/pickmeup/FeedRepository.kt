package com.example.pickmeup

import android.provider.MediaStore

class FeedRepository private constructor(private var feed: MutableList<Feed>) {

    fun getList() : MutableList<Feed> {
        return instance.feed
    }

    companion object {
        private var instance = FeedRepository(arrayListOf())

        fun getInstance() : MutableList<Feed> {
            return instance.getList()
        }

        fun setFeed(feedList : MutableList<Feed>) {
            for(i in feedList.indices) {
                instance.feed[i] = Feed(feedList[i].type, feedList[i].author,
                    feedList[i].category, feedList[i].source)
            }
        }

        fun addFeed(feed : Feed) {
            instance.feed.add(feed)
        }

        fun addFeedList(list : MutableList<Feed>) {
            for(i in list.indices) {
                instance.feed.add(list[i])
            }
        }

        fun emptyFeed() {
            instance = FeedRepository(arrayListOf())
        }
    }
}