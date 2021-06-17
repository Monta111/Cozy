package com.monta.cozy.ui.search

import androidx.lifecycle.MutableLiveData
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.PlaceRepository
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.utils.consts.DEBOUNCE_TIME
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val placeRepository: PlaceRepository) :
    BaseViewModel<SearchEvent>() {

    val isEmptySearchInput = MutableLiveData(true)

    private val searchInput = MutableStateFlow("")

    fun searchPlace(input: String) {
        searchInput.value = input
        isEmptySearchInput.value = input.isBlank()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getSearchResult(): Flow<List<PlaceAutoComplete>> {
        return searchInput
            .debounce(DEBOUNCE_TIME)
            .flatMapLatest { input ->
                placeRepository.searchPlaceAutoComplete(input)
                    .onStart { enableLoading(true) }
                    .onCompletion { enableLoading(false) }
                    .catch {
                        emit(emptyList())
                    }
            }
    }
}