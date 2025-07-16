package com.example.cat_app.ui.components.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cat_app.R
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.viewmodel.BreedsViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState


@Composable
fun OnboardingCarousel(
    breeds: List<BreedsModel>,
    pagerState: PagerState,
    viewModel: BreedsViewModel)
{

    if (breeds.isNotEmpty()) {
    HorizontalPager(
        count = breeds.size ,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) { page ->
        val breed = breeds[page]
        val imageUrl = viewModel.getBreedImageUrl(breed)
        Card (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            elevation = (8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = breed.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    placeholder = painterResource(R.drawable.placehold_error),
                    error = painterResource(R.drawable.ic_launcher_cat_foreground)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = breed.name ?: "Unknown",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    } else {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(top = 64.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}