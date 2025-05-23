package com.android.sigemesapp.presentation.home.search.detail.review

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.Review
import com.android.sigemesapp.data.remote.DeletedMediaObject
import com.android.sigemesapp.data.remote.ReviewMedia
import com.android.sigemesapp.data.remote.response.ReviewResponse
import com.android.sigemesapp.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _reviewResult = MutableLiveData<Result<Review?>>()
    val reviewResult: LiveData<Result<Review?>> get() = _reviewResult

    private val _allReviewResult = MutableLiveData<Result<List<Review>>>()
    val allReviewResult: LiveData<Result<List<Review>>> get() = _allReviewResult

    private val _getReviewResult = MutableLiveData<Result<ReviewResponse>>()
    val getReviewResult: LiveData<Result<ReviewResponse>> get() = _getReviewResult

    private val _rating = MutableLiveData<Int>()
    val rating: LiveData<Int> get() = _rating

    private val _comment = MutableLiveData<String>()
    val comment: LiveData<String> get() = _comment

    private val _photos = MutableLiveData<List<Uri>>()
    val photos: LiveData<List<Uri>> get() = _photos

    private val _newPhotos = MutableLiveData<List<Uri>>()
    val newPhotos: LiveData<List<Uri>> get() = _newPhotos

    private val _downloadedPhotoUris = mutableListOf<Uri>()
    val downloadedPhotoUris: List<Uri> get() = _downloadedPhotoUris

    private val _downloadedMedia = mutableListOf<ReviewMedia>()
    val downloadedMedia: List<ReviewMedia> get() = _downloadedMedia

    private val _deletedMediaObjects = mutableListOf<DeletedMediaObject>()
    val deletedMediaObjects: List<DeletedMediaObject> get() = _deletedMediaObjects

    init {
        _photos.value = emptyList()
    }

    fun addPhotos(newPhotos: List<Uri>) {
        val currentPhotos = _photos.value ?: emptyList()
        _photos.value = currentPhotos + newPhotos
        _downloadedPhotoUris.addAll(currentPhotos + newPhotos)
    }

    fun addNewPhotos(newPhotos: List<Uri>) {
        val currentPhotos = _newPhotos.value ?: emptyList()
        _newPhotos.value = currentPhotos + newPhotos
    }

    fun addDownloadedMedia(media: List<ReviewMedia>) {
        _downloadedMedia.addAll(media)
    }

    fun removePhoto(uri: Uri) {
        val currentPhotos = _photos.value ?: emptyList()
        val currentNewPhotos = _newPhotos.value ?: emptyList()
        _downloadedPhotoUris.remove(uri)
        _newPhotos.value = currentNewPhotos.filter { it != uri }
        _photos.value = currentPhotos.filter { it != uri }
        val removedMedia = _downloadedMedia.find {
            it.uri == uri.toString()
        }
        if (removedMedia != null) {
            _deletedMediaObjects.add(
                DeletedMediaObject(
                    id = removedMedia.id,
                    url = removedMedia.url
                )
            )
            _downloadedMedia.remove(removedMedia)
        }
    }

    fun setRating(rating: Int) {
        _rating.value = rating
    }

    fun setComment(comment: String) {
        _comment.value = comment
    }

    fun getCityHallReviewss(cityhallId: Int){
        viewModelScope.launch {
            sigemesRepository.getCityHallReviews(cityhallId)
                .collect{ result ->
                    _allReviewResult.value = result
                }
        }
    }

    fun getGuesthouseRoomsReviewss(guesthouseId: Int, roomId: Int){
        viewModelScope.launch {
            sigemesRepository.getRoomsReviews(guesthouseId, roomId)
                .collect{ result ->
                    _allReviewResult.value = result
                }
        }
    }

    fun addReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.addReview(rentId, rating, comment, media)
                .collect{ result ->
                    _reviewResult.value = result
                }
        }
    }

    fun getReviewById(rentId: Int){
        viewModelScope.launch {
            sigemesRepository.getReviewById(rentId)
                .collect{ result ->
                    _getReviewResult.value = result
                }
        }
    }

    fun updateReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.updateReview(rentId, reviewId, rating, comment, media, deletedMediaObjects)
                .collect{ result ->
                    _reviewResult.value = result
                }
        }
    }

    @SuppressLint("DefaultLocale")
    fun calculateAverageRatingText(reviews: List<Review>): String {
        if (reviews.isEmpty()) return "0.0 / 5"

        val totalRating = reviews.sumOf { it.rating }
        val average = totalRating.toDouble() / reviews.size
        return String.format("%.1f / 5", average)
    }


    fun calculateTotalReviewText(count: Int): String {
        return " ($count ulasan)"
    }

}
