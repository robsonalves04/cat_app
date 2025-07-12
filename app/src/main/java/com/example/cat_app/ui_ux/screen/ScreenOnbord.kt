package com.example.cat_app.ui_ux.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.cat_app.ui_ux.components.form.OptionsCard
import java.net.URL


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenOnboard(onCardClick: (OnboardOption) -> Unit) {
    val options = onboardOptions

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            OptionsCard(option = option, onClick = { onCardClick(option) })
        }
    }
}


val onboardOptions = listOf(
    OnboardOption("Raças", "Conheça todas as raças", Icons.Default.Pets, route = "list"),
    OnboardOption("Favoritos", "Veja os seus preferidos", Icons.Default.Favorite, route = "favorites"),
    OnboardOption("Sobre", "Sobre o app", Icons.Default.Info, route = "about"),
    OnboardOption("Ajuda", "Central de dúvidas", Icons.Default.Help)
)

data class OnboardOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val image: URL? = null,
    val onClick: (() -> Unit)? = null,
    val route: String? = null

)
