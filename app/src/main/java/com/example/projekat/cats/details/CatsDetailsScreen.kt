package com.example.projekat.cats.details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.domain.Cat
import com.example.projekat.core.compose.AppIconButton
import com.example.projekat.core.compose.NoDataContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projekat.cats.api.model.ImageModel


fun NavGraphBuilder.catsDetails(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) { navBackStackEntry ->
    val dataId = navBackStackEntry.arguments?.getString("id")
        ?: throw IllegalArgumentException("id is required.")

    val catsDetailsViewModel = viewModel<CatsDetailsViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CatsDetailsViewModel(catId = dataId) as T
            }
        },
    )
    val state = catsDetailsViewModel.state.collectAsState()

    CatsDetailsScreen(
        state = state.value,
        onClose = {
            navController.navigateUp()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatsDetailsScreen(
    state: CatsDetailsState,
    onClose: () -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = state.data?.name ?: "Loading") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                navigationIcon = {
                    AppIconButton(
                        imageVector = Icons.Default.ArrowBack,
                        onClick = onClose,
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.fetching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Doslo je do greske")
                    }
                } else if (state.data != null) {
                    state.imageModel?.let {
                        CatColumn(
                            data = state.data,
                            image = it
                        )
                    }
                } else {
                    NoDataContent(id = state.catId)
                }
            }
        }
    )
}

@Composable
private fun CatColumn(
    data: Cat,
    image: ImageModel,
) {
    Column {

        val painter: Painter =
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = image.url).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
            )

        Image(
            painter = painter,
            contentDescription = "Slika",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        if(data.alternativeNames.isNotEmpty()){
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Alternative names: ")
                    }
                    append(data.alternativeNames)
                }
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Description: ")
                }
                append(data.description)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Countries of origin: ")
                }
                append(data.origin)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Temperament: ")
                }
                append(data.temperament)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Life span: ")
                }
                append(data.lifeSpan)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Weight: ")
                }
                append("\n - Metric: ${data.weight.metric}\n - Imperial: ${data.weight.imperial}")
            }
        )


        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Adaptability:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.adaptability)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Affection Level:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.affectionLevel)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Child Friendly:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.childFriendly)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Dog Friendly:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.dogFriendly)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Energy Level:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.energyLevel)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Grooming:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.grooming)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Health Issues:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.healthIssues)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Intelligence:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.intelligence)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Shedding Level:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.sheddingLevel)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Social Needs:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.socialNeeds)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Stranger Friendly:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.strangerFriendly)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = "Vocalisation:",
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = data.vocalisation)

        Spacer(modifier = Modifier.height(16.dp))
        val openWikipedia = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        }
        TextButton(
            onClick = {
                val wikipediaUrl = data.wikipediaURL
                if (wikipediaUrl.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
                    openWikipedia.launch(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.Blue)
                .padding(vertical = 8.dp),
            shape = CircleShape,
        ) {
            Text(
                text = "Open Wikipedia",
                color = Color.White
            )

        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

val goldColor = Color(0xFFFFD700)

@Composable
fun RatingBar(
    rating: Int,
    maxRating: Int = 5,
    filledIcon: ImageVector = Icons.Default.Star,
    emptyIcon: ImageVector = Icons.Default.Star,
    iconTint: Color = goldColor
) {
    Row {
        repeat(maxRating) { index ->
            val icon = if (index < rating) filledIcon else emptyIcon
            val tint = if (index < rating) iconTint else Color.Gray
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f),
                tint = tint
            )
        }
    }
}