package com.android.sigemesapp.presentation.home.search.detail.review

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.model.CityHallReviews
import com.android.sigemesapp.data.model.GuesthouseRoomReviews
import com.android.sigemesapp.data.remote.DeletedMediaObject
import com.android.sigemesapp.data.remote.ReviewMedia
import com.android.sigemesapp.data.remote.response.DetailCityHallReviewResponse
import com.android.sigemesapp.data.remote.response.DetailGuesthouseRoomReviewResponse
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

    private val _cityhallReviewResult = MutableLiveData<Result<CityHallReviews>>()
    val cityhallReviewResult: LiveData<Result<CityHallReviews>> get() = _cityhallReviewResult

    private val _guesthouseReviewResult = MutableLiveData<Result<GuesthouseRoomReviews>>()
    val guesthouseReviewResult: LiveData<Result<GuesthouseRoomReviews>> get() = _guesthouseReviewResult

    private val _guesthouseRoomReviews = MutableLiveData<Result<List<GuesthouseRoomReviews>>>()
    val guesthouseRoomReviews: LiveData<Result<List<GuesthouseRoomReviews>>> get() = _guesthouseRoomReviews

    private val _cityHallReviews = MutableLiveData<Result<List<CityHallReviews>>>()
    val cityHallReviews: LiveData<Result<List<CityHallReviews>>> get() = _cityHallReviews

    private val _cityHallRentReviewById = MutableLiveData<Result<DetailCityHallReviewResponse>>()
    val cityHallRentReviewById: LiveData<Result<DetailCityHallReviewResponse>> get() = _cityHallRentReviewById

    private val _guesthouseRentReviewById = MutableLiveData<Result<DetailGuesthouseRoomReviewResponse>>()
    val guesthouseRentReviewById: LiveData<Result<DetailGuesthouseRoomReviewResponse>> get() = _guesthouseRentReviewById

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

    fun getCityHallReviews(cityhallId: Int){
        viewModelScope.launch {
            sigemesRepository.getCityHallReview(cityhallId)
                .collect{ result ->
                    _cityHallReviews.value = result
                }
        }
    }

    fun getGuesthouseRoomsReviews(guesthouseId: Int, roomId: Int){
        viewModelScope.launch {
            sigemesRepository.getRoomsReview(guesthouseId, roomId)
                .collect{ result ->
                    _guesthouseRoomReviews.value = result
                }
        }
    }

    fun addGuesthouseReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.addRoomReview(rentId, rating, comment, media)
                .collect{ result ->
                    _guesthouseReviewResult.value = result
                }
        }
    }

    fun addCityHallReview(
        rentId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.addCityHallReview(rentId, rating, comment, media)
                .collect{ result ->
                    _cityhallReviewResult.value = result
                }
        }
    }

    fun getCityHallReviewById(rentId: Int){
        viewModelScope.launch {
            sigemesRepository.getCityHallReviewById(rentId)
                .collect{ result ->
                    _cityHallRentReviewById.value = result
                }
        }
    }

    fun getGuesthouseReviewById(rentId: Int){
        viewModelScope.launch {
            sigemesRepository.getRoomReviewById(rentId)
                .collect{ result ->
                    _guesthouseRentReviewById.value = result
                }
        }
    }

    fun updateGuesthouseReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.updateRoomReview(rentId, reviewId, rating, comment, media, deletedMediaObjects)
                .collect{ result ->
                    _guesthouseReviewResult.value = result
                }
        }
    }

    fun updateCityHallReview(
        rentId: Int,
        reviewId: Int,
        rating: Int,
        comment: String,
        media: List<File?> = emptyList(),
        deletedMediaObjects: List<DeletedMediaObject> = emptyList()
    ){
        viewModelScope.launch {
            sigemesRepository.updateCityHallReview(rentId, reviewId, rating, comment, media, deletedMediaObjects)
                .collect{ result ->
                    _cityhallReviewResult.value = result
                }
        }
    }

    @SuppressLint("DefaultLocale")
    fun calculateAverageRatingText(reviews: List<Any>): String {
        val totalRating = reviews.sumOf {
            when (it) {
                is GuesthouseRoomReviews -> it.rating
                is CityHallReviews -> it.rating
                else -> 0
            }
        }
        val average = if (reviews.isNotEmpty()) totalRating.toDouble() / reviews.size else 0.0
        return String.format("%.1f / 5", average)
    }

    fun calculateTotalReviewText(count: Int): String {
        return " ($count ulasan)"
    }

}
