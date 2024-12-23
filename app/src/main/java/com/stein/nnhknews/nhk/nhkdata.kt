package com.stein.nnhknews.nhk

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.*

@Entity(tableName = "news")
@Serializable
data class NhkNews(
        @PrimaryKey val newsId: String,
        val title: String,
        val titleWithRuby: String,
        val outline: String?,
        val outlineWithRuby: String,
        val body: String,
        val bodyWithoutHtml: String?,
        val url: String,
        val m3u8Url: String,
        val imageUrl: String,
        val publishedAtUtc: String,
)

fun NhkNews.newsHtml(): String {
    var image = ""
    if (imageUrl != "") {
        image = "<img src=${imageUrl} />"
    }

    return """
      <!DOCTYPE html>
      <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body>
          <style>
            body {
              padding-bottom: 80px;
            }
            * {
              font-size: 16px;
            }
            h1 {
              font-size: 20px;
            }
            rt {
              font-size: 12px;
            }
            img {
              max-width: 100%;
            }
            .under {
              text-decoration-line: underline;
              -webkit-text-decoration-line: underline;
              text-decoration-color: #ff7f00;
              -webkit-text-decoration-color: #ff7f00;
            }
          </style>
          $image
          <h1>${titleWithRuby}</h1>
          ${body}
        </body>
      </html>
      """
}
