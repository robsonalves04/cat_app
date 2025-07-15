package com.example.cat_app.ui_ux.components.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.cat_app.data.models.BreedsModel

@Composable
fun BreedDialog(
    breed: BreedsModel,
    onDismiss: () -> Unit,
) {
    val imageUrl = breed.referenceImageId?.let { "https://cdn2.thecatapi.com/images/$it.jpg" }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = breed.name!!,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = breed.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Origin: ") }
                    append(breed.origin ?: "Desconhecida")
                })
                Spacer(modifier = Modifier.height(6.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Temperament: ") }
                    append(breed.temperament ?: "Não informado")
                })
                Spacer(modifier = Modifier.height(6.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Description: ") }
                    append(breed.description ?: "Sem descrição")
                })
                Spacer(modifier = Modifier.height(6.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Life Span: ") }
                    append("${breed.lifeSpan ?: "Indisponível"} anos")
                })
                Spacer(modifier = Modifier.height(6.dp))
                breed.weight?.metric?.let { weight ->
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Weight (kg): ") }
                        append(weight)
                    })
                }



                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF102E56),
                        contentColor = Color.White
                    )
                ) {
                    Text("Fechar")
                }
            }
        }
    }
}
