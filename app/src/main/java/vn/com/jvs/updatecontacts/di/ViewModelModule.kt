package vn.com.jvs.updatecontacts.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.com.jvs.updatecontacts.viewModel.MainViewModel

public val viewModelModule = module {
    viewModel { MainViewModel() }
}