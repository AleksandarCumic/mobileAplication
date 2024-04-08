package com.example.projekat.cats.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.R
import com.example.projekat.cats.domain.Cat
import com.example.projekat.core.compose.AppIconButton
import com.example.projekat.core.compose.NoDataContent
import com.example.projekat.cats.list.CatsDetailsUiEvent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun NavGraphBuilder.catsDetails(
    route: String,
    navController: NavController,
) = composable(
    route = route,
) { navBackStackEntry ->
    val dataId = navBackStackEntry.arguments?.getString("id")
        ?: throw IllegalArgumentException("id is required.")

    // We have to provide factory class to instantiate our view model
    // since it has a custom property in constructor
    val catsDetailsViewModel = viewModel<CatsDetailsViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // We pass the passwordId which we read from arguments above
                return CatsDetailsViewModel(catId = dataId) as T
            }
        },
    )
    val state = catsDetailsViewModel.state.collectAsState()

    CatsDetailsScreen(
        state = state.value,
        eventPublisher = {
            catsDetailsViewModel.setEvent(it)
        },
        onEditClick = {
            navController.navigate(route = "passwords/editor?id=${state.value.catId}")
        },
        onClose = {
            navController.navigateUp()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatsDetailsScreen(
    state: CatsDetailsState,
    eventPublisher: (CatsDetailsUiEvent) -> Unit,
    onEditClick: () -> Unit,
    onClose: () -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = state.data?.nameofTheBreed ?: "Loading")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                actions = {
                    if (state.data != null) {
                        DeleteAppIconButton(
                            onDeleteConfirmed = {
                                eventPublisher(
                                    CatsDetailsUiEvent.RequestCatDelete(
                                        catId = state.catId
                                    )
                                )
                                onClose()
                            }
                        )

                        AppIconButton(
                            imageVector = Icons.Default.Edit,
                            onClick = onEditClick,
                        )
                    }
                },
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
                        val errorMessage = when (state.error) {
                            is CatsDetailsState.DetailsError.DataUpdateFailed ->
                                "Failed to load. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)
                    }
                } else if (state.data != null) {
                    CatColumn(
                        data = state.data,
                    )
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
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineSmall,
            text = data.nameofTheBreed,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = data.descriptionShort,
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DeleteAppIconButton(
    onDeleteConfirmed: () -> Unit,
) {
    var confirmationDeleteShown by remember { mutableStateOf(false) }
    if (confirmationDeleteShown) {
        AlertDialog(
            onDismissRequest = { confirmationDeleteShown = false },
            title = { Text(text = stringResource(id = R.string.editor_delete_confirmation_title)) },
            text = {
                Text(
                    text = stringResource(R.string.editor_delete_confirmation_simple_text)
                )
            },
            dismissButton = {
                TextButton(onClick = { confirmationDeleteShown = false }) {
                    Text(text = stringResource(id = R.string.editor_delete_confirmation_dismiss))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmationDeleteShown = false
                    onDeleteConfirmed()
                }) {
                    Text(text = stringResource(id = R.string.editor_delete_confirmation_yes))
                }
            },
        )
    }

    AppIconButton(
        imageVector = Icons.Default.Delete,
        onClick = { confirmationDeleteShown = true },
    )
}

@Composable
fun RatingBar(
    rating: Int,
    maxRating: Int = 5,
    filledIcon: ImageVector = Icons.Default.Star,
    emptyIcon: ImageVector = Icons.Default.Star,
    iconTint: Color = Color.Yellow
) {
    Row {
        repeat(maxRating) { index ->
            val icon = if (index < rating) filledIcon else emptyIcon
            val tint = if (index < rating) iconTint else Color.Gray
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .weight(1f),
                tint = tint
            )
        }
    }
}