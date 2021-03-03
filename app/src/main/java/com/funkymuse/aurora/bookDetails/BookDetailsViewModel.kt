package com.funkymuse.aurora.bookDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crazylegend.kotlinextensions.log.debug
import com.crazylegend.retrofit.apiCall
import com.crazylegend.retrofit.retrofitResult.RetrofitResult
import com.crazylegend.retrofit.retrofitResult.loading
import com.crazylegend.retrofit.retrofitResult.retrofitLoading
import com.funkymuse.aurora.api.LibgenAPI
import com.funkymuse.aurora.bottomNav.favorites.FavoritesDAO
import com.funkymuse.aurora.dto.DetailedBookModel
import com.funkymuse.aurora.dto.FavoriteBook
import com.funkymuse.aurora.mirrorsDB.MirrorDao
import com.funkymuse.aurora.mirrorsDB.MirrorModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Created by FunkyMuse, date 2/28/21
 */
class BookDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    private val libgenAPI: LibgenAPI,
    private val mirrorDao: MirrorDao,
    private val favoritesDAO: FavoritesDAO
) : ViewModel() {

    @AssistedFactory
    interface BookDetailsVMF {
        fun create(id: Int): BookDetailsViewModel
    }

    private val booksData: MutableStateFlow<RetrofitResult<List<DetailedBookModel>>> =
        MutableStateFlow(RetrofitResult.EmptyData)
    val book = booksData.asStateFlow()

    private val bookMirrorsData: MutableStateFlow<MirrorModel?> = MutableStateFlow(null)
    val bookMirrors = bookMirrorsData.asStateFlow()

     private val favoriteBookData: MutableStateFlow<FavoriteBook?> = MutableStateFlow(null)
    val favoriteBook = favoriteBookData.asStateFlow()



    init {
        viewModelScope.launch {
            val books = async {
                booksData.value = retrofitLoading
                libgenAPI.getDetailedBook(id)
            }
            val mirrors = async { mirrorDao.getMirrorModelForBookId(id) }
            val favorite = async { favoritesDAO.getFavoriteById(id) }
            favorite.await().onEach {
                favoriteBookData.value = it
            }.launchIn(viewModelScope)
            bookMirrorsData.value = mirrors.await()
            booksData.value = books.await()
        }
    }

    fun deleteBookMirrors() {
        viewModelScope.launch {
            mirrorDao.deleteMirrorModel(id)
        }
    }

    fun addToFavorites(favoriteBook: FavoriteBook) {
        viewModelScope.launch { favoritesDAO.insertIntoFavorites(favoriteBook) }
    }

    fun removeFromFavorites(favoriteBookID: Int) {
        viewModelScope.launch { favoritesDAO.deleteFromFavoritesByID(favoriteBookID) }
    }
}