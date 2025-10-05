package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import com.domain.usecases.GetUserDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDetailScreenViewModel @Inject constructor(
    private val getUserDetailUseCase: GetUserDetailUseCase,
) : ViewModel() {

}