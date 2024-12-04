package app.test.networkapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.test.networkapp.data.models.Organization
import app.test.networkapp.utils.toFormattedString

@Composable
fun OrganizationItem(
    organization: Organization,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            organization.country?.let { countryCode ->
                CountryFlag(
                    countryCode = countryCode,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
            }

            Column {
                Text(
                    text = organization.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "ID: ${organization.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Дата создания: ${organization.created?.toFormattedString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}