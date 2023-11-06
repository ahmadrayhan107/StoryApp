package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val stories = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-$i-dummy-pic.png",
                "2022-01-08T06:34:$i.598Z",
                "Nama $i",
                "Deskripsi $i",
                "16.00$i".toDouble(),
                i.toString(),
                "-10.00$i".toDouble()
            )
            items.add(stories)
        }
        return items
    }
}