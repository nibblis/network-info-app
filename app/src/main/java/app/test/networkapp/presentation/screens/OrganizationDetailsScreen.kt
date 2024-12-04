package app.test.networkapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.test.networkapp.presentation.components.CountryFlag
import app.test.networkapp.presentation.components.NetworkItem
import app.test.networkapp.presentation.viewmodels.OrganizationDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationDetailsScreen(
    organizationId: String,
    onBackPressed: () -> Unit,
    onNetworkSelected: (String) -> Unit,
) {
    val viewModel = koinViewModel<OrganizationDetailsViewModel> { parametersOf(organizationId) }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Детали организации") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                state.organization?.let { org ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = org.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            org.country?.let { country ->
                                CountryFlag(
                                    countryCode = country,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(top = 8.dp)
                                )
                            }
                            Text(
                                text = "ID: ${org.id}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Text(
                    text = "Сети организации",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn {
                    items(state.networks) { network ->
                        NetworkItem(
                            network = network,
                            onClick = { onNetworkSelected(network.inetnum) }
                        )
                    }
                }
            }
            if (state.showProgressBar) {
                CircularProgressIndicator()
            }
        }
    }
}