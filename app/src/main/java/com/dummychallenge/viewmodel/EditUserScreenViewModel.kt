package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import com.domain.usecases.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditUserScreenViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

}