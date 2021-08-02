package com.funkymuse.aurora.favoritebookmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.funkymuse.aurora.generalbook.GeneralBook
import com.funkymuse.aurora.serverconstants.COVERS_APPEND


/**
 * Created by FunkyMuse on 25/02/21 to long live and prosper !
 */

@Entity(tableName = "favorite_books")
data class FavoriteBook(

    @PrimaryKey
    @ColumnInfo(name = "id")
    override val id: String = "",

    @ColumnInfo(name = "title")
    override val title: String? = null,

    @ColumnInfo(name = "image")
    val realImage: String? = null,

    @ColumnInfo(name = "author")
    override val author: String? = null,

    @ColumnInfo(name = "extension")
    override val extension: String? = null,

    @ColumnInfo(name = "pages")
    override val pages: String? = null,

    @ColumnInfo(name = "size")
    val favoriteSize: String? = null,

    @ColumnInfo(name = "year")
    override val year: String? = null,

    ) : GeneralBook {

    override val size get() = favoriteSize?.toLongOrNull()?.toFileSizeString()
    override val image: String get() = COVERS_APPEND + realImage

    @Ignore
    private val fileSizeUnits = arrayOf("bytes", "Kb", "Mb", "GB", "TB", "PB", "EB", "ZB", "YB")

    private fun Long.toFileSizeString(): String {
        var bytesToCalculate = this
        val sizeToReturn: String
        var index = 0
        while (index < fileSizeUnits.size) {
            if (bytesToCalculate < 1024) {
                break
            }
            bytesToCalculate /= 1024
            index++
        }
        sizeToReturn = bytesToCalculate.toString() + " " + fileSizeUnits[index]
        return sizeToReturn
    }

}