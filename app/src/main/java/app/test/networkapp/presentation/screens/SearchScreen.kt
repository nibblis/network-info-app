package app.test.networkapp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.test.networkapp.presentation.components.NetworkItem
import app.test.networkapp.presentation.components.OrganizationItem
import app.test.networkapp.presentation.viewmodels.SearchState
import app.test.networkapp.presentation.viewmodels.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onOrganizationSelected: (String) -> Unit,
    onNetworkSelected: (String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResult by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Поиск") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Введите IP или название организации") }
            )

            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = { viewModel.search(searchQuery) }
            ) {
                Text("Найти")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val result = searchResult) {
                is SearchState.Organizations -> {
                    LazyColumn {
                        items(result.data) { org ->
                            OrganizationItem(
                                organization = org,
                                onClick = { onOrganizationSelected(org.id) }
                            )
                        }
                    }
                }

                is SearchState.Networks -> {
                    LazyColumn {
                        items(result.data) { network ->
                            NetworkItem(
                                network = network,
                                onClick = { onNetworkSelected(network.inetnum) }
                            )
                        }
                    }
                }

                is SearchState.Loading -> CircularProgressIndicator()
                is SearchState.Error -> Text("Ошибка: ${result.message}")
                is SearchState.Initial -> Text("Начните поиск")
            }
        }
    }
}