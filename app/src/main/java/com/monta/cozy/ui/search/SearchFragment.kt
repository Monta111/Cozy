package com.monta.cozy.ui.search

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.monta.cozy.R
import com.monta.cozy.base.SpeechRecognitionFragment
import com.monta.cozy.databinding.FragmentSearchBinding
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.ui.adapter.PlaceAutoCompleteAdapter
import com.monta.cozy.utils.consts.PLACE_ID_KEY
import com.monta.cozy.utils.consts.PLACE_REQUEST_KEY
import com.monta.cozy.utils.consts.SEARCH_INPUT_KEY
import com.monta.cozy.utils.consts.SEARCH_REQUEST_KEY
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class SearchFragment : SpeechRecognitionFragment<FragmentSearchBinding, SearchViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_search

    override val viewModel by viewModels<SearchViewModel> { viewModelFactory }

    private var placeAdapter: PlaceAutoCompleteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(SEARCH_REQUEST_KEY) { _, bundle ->
            bundle.getString(SEARCH_INPUT_KEY)?.let { searchPlace(it) }
        }
    }

    override fun setupView() {
        super.setupView()
        with(binding) {
            cvSearch.setMargins(top = getStatusBarHeight() + getDimen(R.dimen.normal_margin).toInt())

            tvSearch.showKeyboard()
            tvSearch.doAfterTextChanged { input ->
                input?.let { this@SearchFragment.viewModel.searchPlace(it.toString()) }
            }

            rcvPlaceAutocomplete.adapter = PlaceAutoCompleteAdapter(object :
                PlaceAutoCompleteAdapter.OnPlaceAutoCompleteClickListener {
                override fun onPlaceAutoCompleteClick(p: PlaceAutoComplete) {
                    setFragmentResult(PLACE_REQUEST_KEY, bundleOf(PLACE_ID_KEY to p.placeId))
                    onBackPressed()
                }
            }).also { this@SearchFragment.placeAdapter = it }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.getSearchResult()
                .onEach { placeAdapter?.submitList(it) }
                .collect()
        }
    }

    private fun searchPlace(input: String) {
        binding.tvSearch.apply {
            setText(input)
            post { setSelection(input.length) }
        }
    }

    override fun onRecognizeSuccess(result: String) {
        searchPlace(result)
    }

    fun clearSearchText() {
        binding.tvSearch.setText("")
    }

    override fun onStart() {
        super.onStart()
        hideBottomNav()
    }

    override fun onStop() {
        super.onStop()
        showBottomNav()
    }

    companion object {
        const val TAG = "SearchFragment"
    }
}