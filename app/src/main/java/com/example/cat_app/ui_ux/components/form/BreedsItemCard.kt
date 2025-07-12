package com.example.cat_app.ui_ux.components.form

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cat_app.R
import com.example.cat_app.data.models.BreedsModel

@Composable
fun BreedItemCard(breed: BreedsModel) {

    val context = LocalContext.current
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                Toast
                    .makeText(context, "Clicou: ${breed.name}", Toast.LENGTH_SHORT)
                    .show()
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row (modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = breed.image?.url,
                contentDescription = "Imagem de ${breed.name}",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = painterResource(id = R.drawable.placehold_error),
                error = painterResource(id = R.drawable.ic_launcher_cat_foreground),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                breed.name?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
                breed.origin?.let {
                    Text(text = "Origem: $it", style = MaterialTheme.typography.bodySmall)
                }
                breed.temperament?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}
