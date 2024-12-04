package app.test.networkapp.di

import app.test.networkapp.data.repository.NetworkRepository
import app.test.networkapp.data.repository.OrganizationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::OrganizationRepository)
    singleOf(::NetworkRepository)
}