package com.dummychallenge.viewmodel

import androidx.lifecycle.ViewModel
import com.domain.usecases.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateUserScreenViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

}