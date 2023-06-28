package com.bangkit23.storygram.utils

import com.bangkit23.storygram.data.remote.response.ListStoryItem
import kotlin.text.Typography.quote

object DataDummy {
    val name: String = "sas"

    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photo $i",
                "created date + $i",
                "name $i",
                "desc $i",
                1.3,
                "id $i",
                1.4
            )
            items.add(story)
        }
        return items

    }
}