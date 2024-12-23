package com.stein.nnhknews.nhk

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDateTime(input: String): String {
    // Parse the input as a ZonedDateTime
    val zonedDateTime = ZonedDateTime.parse(input)

    // Convert to LocalDateTime in the system's default time zone
    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

    // Format the LocalDateTime as "yyyy-MM-dd HH:mm"
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return localDateTime.format(formatter)
}

@Composable
fun NhkNews.NewsView(dp: PaddingValues? = null, onClicked: () -> Unit = {}) {
    val glModifier =
            Modifier.padding(all = 8.dp).fillMaxSize().let done@{
                if (dp == null) return@done it
                it.padding(dp)
            }
    Card(modifier = glModifier.clickable { onClicked() }) {
        Row {
            AsyncImage(
                    model = imageUrl,
                    contentDescription = "image",
                    modifier = Modifier.width(120.dp).height(100.dp)
            )
            Column {
                Text(text = title, modifier = Modifier.padding(10.dp))
                Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd // Aligns the text to the end
                ) {
                    Text(
                            text = formatDateTime(publishedAtUtc),
                            modifier = Modifier.padding(6.dp),
                            fontSize = 15.sp
                    )
                }
            }
        }
    }
}
