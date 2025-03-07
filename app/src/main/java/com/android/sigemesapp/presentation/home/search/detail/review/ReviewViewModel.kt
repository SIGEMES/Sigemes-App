package com.android.sigemesapp.presentation.home.search.detail.review

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sigemesapp.data.source.remote.response.AddedCityHallReview
import com.android.sigemesapp.data.source.remote.response.AddedGuesthouseReview
import com.android.sigemesapp.data.source.remote.response.CityHallReviews
import com.android.sigemesapp.data.source.remote.response.GetReviewCityHallByIdResponse
import com.android.sigemesapp.data.source.remote.response.GetReviewGuesthouseByIdResponse
import com.android.sigemesapp.data.source.remote.response.GuesthouseRoomReviews
import com.android.sigemesapp.domain.repository.SigemesRepository
import com.android.sigemesapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val sigemesRepository: SigemesRepository
) : ViewModel() {

    private val _cityhallReviewResult = MutableLiveData<Result<AddedCityHallReview>>()
    val cityhallReviewResult: LiveData<Result<AddedCityHallReview>> get() = _cityhallReviewResult

    private val _guesthouseReviewResult = MutableLiveData<Result<AddedGuesthouseReview>>()
    val guesthouseReviewResult: LiveData<Result<AddedGuesthouseReview>> get() = _guesthouseReviewResult

    private val _guesthouseRoomReviews = MutableLiveData<Result<List<GuesthouseRoomReviews>>>()
    val guesthouseRoomReviews: LiveData<Result<List<GuesthouseRoomReviews>>> get() = _guesthouseRoomReviews

    private val _cityHallReviews = MutableLiveData<Result<List<CityHallReviews>>>()
    val cityHallReviews: LiveData<Result<List<CityHallReviews>>> get() = _cityHallReviews

    private val _cityHallRentReviewById = MutableLiveData<Result<GetReviewCityHallByIdResponse>>()
    val cityHallRentReviewById: LiveData<Result<GetReviewCityHallByIdResponse>> get() = _cityHallRentReviewById

    private val _guesthouseRentReviewById = MutableLiveData<Result<GetReviewGuesthouseByIdResponse>>()
    val guesthouseRentReviewById: LiveData<Result<GetReviewGuesthouseByIdResponse>> get() = _guesthouseRentReviewById

    private val _rating = MutableLiveData<Int>()
    val rating: LiveData<Int> get() = _rating

    private val _comment = MutableLiveData<String>()
    val comment: LiveData<String> get() = _comment

    private val _photos = MutableLiveData<List<Uri>>()
    val photos: LiveData<List<Uri>> get() = _photos

    init {
        _photos.value = emptyList()
    }

    fun addPhotos(newPhotos: List<Uri>) {
        val currentPhotos = _photos.value ?: emptyList()
        _photos.value = currentPhotos + newPhotos
    }

    fun removePhoto(uri: Uri) {
        val currentPhotos = _photos.value ?: emptyList()
        _photos.value = currentPhotos.filter { it != uri }
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
            sigemesRepository.getGuesthouseRoomsReview(guesthouseId, roomId)
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
            sigemesRepository.addGuesthouseReview(rentId, rating, comment, media)
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

    fun getCityHallReviewById(rent_id: Int){
        viewModelScope.launch {
            sigemesRepository.getCityHallReviewById(rent_id)
                .collect{ result ->
                    _cityHallRentReviewById.value = result
                }
        }
    }

    fun getGuesthouseReviewById(rent_id: Int){
        viewModelScope.launch {
            sigemesRepository.getGuesthouseReviewById(rent_id)
                .collect{ result ->
                    _guesthouseRentReviewById.value = result
                }
        }
    }

}
