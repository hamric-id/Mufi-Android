package com.hamric.feature.details.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hamric.core.model.AuthorDetails
import com.hamric.core.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isLongContent = review.content.length > 100

    val formattedDate = formatDate(review.createdAt)

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = review.authorDetails?.avatarPath?.let {
                        if (it.startsWith("/")) {
                            "https://image.tmdb.org/t/p/w45$it"
                        } else {
                            it
                        }
                    } ?: "https://ui-avatars.com/api/?name=${review.author}&background=random&size=45",
                    contentDescription = review.author,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                review.authorDetails?.rating?.let { rating ->
                    Text(
                        text = "⭐ $rating/10",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 0
                        )
                    )
            ) {
                if (!isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = review.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = review.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Visible
                        )
                    }
                }

                if (isLongContent) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (isExpanded) "Minimize" else "Read More",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}


fun formatDate(dateString: String): String {
    return try {
        val inputFormat = when {
            dateString.contains("T") -> {
                if (dateString.contains(".")) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                } else {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                }
            }
            else -> {
                SimpleDateFormat("yyyy-MM-dd", Locale.US)
            }
        }

        val outputFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.US)
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

@Preview(
    name = "Review Items Preview",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun ReviewItemPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ReviewItem(
            review = createSampleReview(
                author = "Jane Smith",
                content = "This movie exceeded all my expectations. The story was incredibly well-written with multiple plot twists that kept me on the edge of my seat. The character development was outstanding, and the emotional depth of the performances really resonated with me. I've seen it three times already and I still notice new details each time. The soundtrack was also phenomenal and perfectly complemented the mood of each scene. If you haven't seen this yet, you're missing out on one of the best films of the decade! ⭐⭐⭐⭐⭐",
                rating = 8.7,
                avatarPath = "/3bHtAsuz7Zf7d8M4zGdHh2y8K8V.jpg",
                createdAt = "2024-06-20T14:45:00.000Z"
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        ReviewItem(
            review = createSampleReview(
                author = "MovieBuff2024",
                content = "Solid film with great visuals and a compelling narrative. The pacing was a bit slow in the middle, but the climax made up for it. Definitely worth watching!",
                rating = 7.2,
                avatarPath = null,
                createdAt = "2024-03-10T08:15:00.000Z"
            )
        )
    }
}


fun createSampleReview(
    id: String = "1",
    author: String = "John Doe",
    content: String = "Sample review content",
    rating: Double? = 8.5,
    avatarPath: String? = null,
    createdAt: String = "2024-01-15T10:30:00.000Z"
): Review {
    return Review(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt,
        authorDetails = AuthorDetails(
            name = author,
            username = author.lowercase().replace(" ", ""),
            avatarPath = avatarPath,
            rating = rating
        )
    )
}