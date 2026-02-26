package app.test.networkapp.presentation.organization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrganizationScreen(viewModel: OrganizationViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is OrganizationUiState.Loading -> {
                CircularProgressIndicator()
            }
            is OrganizationUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.organizations) { organization ->
                        Text(text = organization.name)
                    }
                }
            }
            is OrganizationUiState.Error -> {
                Text(text = state.message)
            }
        }
    }
}
