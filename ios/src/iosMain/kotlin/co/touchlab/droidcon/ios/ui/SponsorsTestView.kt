package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorGroupViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorListViewModel
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter
import io.ktor.http.URLParserException
import io.ktor.http.Url
import kotlin.math.min

@Composable
internal fun SponsorsTestView(viewModel: SponsorListViewModel) {
    val sponsorGroups by viewModel.observeSponsorGroups.observeAsState()

    Column {
        if (sponsorGroups.isEmpty()) {
            EmptyView()
        } else {
            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                items(sponsorGroups) { sponsorGroup ->
                    SponsorGroupView(sponsorGroup)
                }
            }
        }
    }
}

@Composable
private fun SponsorGroupView(sponsorGroup: SponsorGroupViewModel) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        color = MaterialTheme.colors.background,
        elevation = 2.dp,
        border = if (MaterialTheme.colors.isLight) null else BorderStroke(1.dp, MaterialTheme.colors.surface),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = sponsorGroup.title,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 4.dp,
                ),
                style = MaterialTheme.typography.h4,
            )
            val columnCount = if (sponsorGroup.isProminent) 3 else 4

            val sponsors by sponsorGroup.observeSponsors.observeAsState()

            repeat(sponsors.size / columnCount + if (sponsors.size % columnCount == 0) 0 else 1) { rowIndex ->
                Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                    val startIndex = rowIndex * columnCount
                    val endIndex = min(startIndex + columnCount, sponsors.size)
                    sponsors.subList(startIndex, endIndex).forEach { sponsor ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .shadow(2.dp, CircleShape)
                                .background(Color.White)
                                .clickable {
                                    sponsor.selected()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            val imageUrl = sponsor.validImageUrl
                            if (imageUrl != null) {
                                CompositionLocalProvider(
                                    LocalImageLoader provides ImageLoaderBuilder().build(),
                                ) {
                                    val resource = rememberAsyncImagePainter(
                                        url = imageUrl.toString(),
                                        imageLoader = LocalImageLoader.current,
                                    )

                                    Image(
                                        painter = resource,
                                        contentDescription = sponsor.name,
                                    )
                                }
                            } else {
                                Text(
                                    text = sponsor.name,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 3,
                                )
                            }
                        }
                    }
                    repeat(columnCount - endIndex + startIndex) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(16.dp),
            tint = Color.Yellow,
        )

        Text(
            text = "Sponsors could not be loaded.",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

fun getRootController(viewModel: SponsorListViewModel) = Application("SponsorsTestView") {
    SponsorsTestView(viewModel)
}
