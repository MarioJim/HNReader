package org.team4.hnreader.ui.callbacks

import org.team4.hnreader.data.model.Story

interface StoryIdsSourceAndClickHandler {
    fun fetchStoryIds(
        responseCallback: (List<Int>) -> Unit,
        errorCallback: (Exception) -> Unit,
    )

    fun openComments(story: Story)
}
