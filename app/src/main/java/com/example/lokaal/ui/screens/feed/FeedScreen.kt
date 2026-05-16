package com.example.lokaal.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.ui.screens.feed.components.EmptyFeed
import com.example.lokaal.ui.screens.feed.components.ErrorItem
import com.example.lokaal.ui.screens.feed.components.FeedTopBar
import com.example.lokaal.ui.screens.feed.components.LoadingFooter
import com.example.lokaal.ui.screens.feed.components.MomentCard
import com.example.lokaal.ui.theme.LokaalTheme
import kotlinx.coroutines.flow.flowOf
import androidx.paging.PagingData

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val moments = viewModel.moments.collectAsLazyPagingItems()
    FeedContent(
        modifier = modifier,
        moments = moments
    )
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    moments: LazyPagingItems<Moment>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeedTopBar()

        when {
            // Initial loading
            moments.loadState.refresh is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Initial error
            moments.loadState.refresh is LoadState.Error -> {
                val error = moments.loadState.refresh as LoadState.Error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = error.error.message ?: "Something went wrong",
                        onRetry = { moments.retry() }
                    )
                }
            }

            // Empty state
            moments.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyFeed()
                }
            }

            // Content
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(
                        count = moments.itemCount,
                        key = moments.itemKey { it.id }
                    ) { index ->
                        val moment = moments[index]
                        if (moment != null) {
                            MomentCard(moment = moment)
                        }
                    }

                    // Append loading / error
                    when (val append = moments.loadState.append) {
                        is LoadState.Loading -> {
                            item { LoadingFooter() }
                        }
                        is LoadState.Error -> {
                            item {
                                ErrorItem(
                                    message = append.error.message ?: "Failed to load more",
                                    onRetry = { moments.retry() }
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedContentPreview() {
    val momentsList = listOf(
        Moment(
            id = "1",
            authorName = "John Doe",
            caption = "Exploring the hidden gems of the city!",
            locationName = "Downtown, Cairo"
        ),
        Moment(
            id = "2",
            authorName = "Jane Smith",
            caption = "Delicious street food you must try.",
            locationName = "Zamalek, Cairo"
        )
    )
    val momentsFlow = flowOf(PagingData.from(momentsList))
    val lazyPagingItems = momentsFlow.collectAsLazyPagingItems()

    LokaalTheme {
        FeedContent(moments = lazyPagingItems)
    }
}

