package app.test.networkapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun CountryFlag(
    countryCode: String,
    modifier: Modifier = Modifier,
) {
    val flagUrl = "https://flagsapi.com/${countryCode.uppercase()}/flat/64.png"

    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(flagUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        ),
        contentDescription = "Flag of $countryCode",
        modifier = modifier
    )
}