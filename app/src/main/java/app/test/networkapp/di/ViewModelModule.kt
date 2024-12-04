package app.test.networkapp.di

import app.test.networkapp.presentation.viewmodels.NetworkDetailsViewModel
import app.test.networkapp.presentation.viewmodels.OrganizationDetailsViewModel
import app.test.networkapp.presentation.viewmodels.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SearchViewModel)
    viewModel { (organizationId: String) ->
        OrganizationDetailsViewModel(organizationId, get())
    }
    viewModel { (inetnum: String) ->
        NetworkDetailsViewModel(inetnum, get())
    }
}