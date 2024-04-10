package com.example.projekat.cats.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.repository.SampleData
import com.example.projekat.core.theme.ProjekatTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring


@ExperimentalMaterial3Api
fun NavGraphBuilder.catsListScreen(
    route: String,
    navController: NavController,
) = composable(route = route) {
    val catListViewModel = viewModel<CatsListViewModel>()
    val state by catListViewModel.state.collectAsState()

    CatListScreen(
        state = state,

        onItemClick = {
            navController.navigate(route = "cats/${it.id}")
        },
    )
}


@ExperimentalMaterial3Api
@Composable
fun CatListScreen(
    state: CatsListState?,
    onItemClick: (Cat) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "List of Cats") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
        content = {
            state?.let { currentState ->
                CatList(
                    paddingValues = it,
                    items = currentState.cats,
                    onItemClick = onItemClick,
                    fetching = currentState.fetching,
                    error = currentState.error,
                )
            }
        }
    )
}

@Composable
private fun CatList(
    items: List<Cat>,
    paddingValues: PaddingValues,
    onItemClick: (Cat) -> Unit,
    fetching: Boolean,
    error: CatsListState.ListError?,
) {
    // LazyColumn should be used for infinite lists which we will
    // learn soon. In the meantime we can use Column with verticalScroll
    // modifier so it can be scrollable.
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        items.forEach {
            Column {
                key(it.id) {
                    LoginListItem(
                        data = it,
                        onClick = {
                            onItemClick(it)
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (items.isEmpty()) {
            when {
                fetching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (error) {
                            is CatsListState.ListError.ListUpdateFailed ->
                                "Failed to load. Error message: ${error.cause?.message}."
                            else -> "Unknown error occurred."
                        }
                        Text(
                            text = errorMessage,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "No cats.", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}



@Composable
private fun LoginListItem(
    data: Cat,
    onClick: () -> Unit,

) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = buildString {
                    append(data.name)
                    if (!data.alternativeNames.isNullOrEmpty()) {
                        append(" (${data.alternativeNames})")
                    }
                },
//                text = data.name,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.description.take(250).plus("..."),
                style = TextStyle(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val temperamentList = uzmiTri(data).joinToString(", ") // Prikaži tri nasumično odabrana temperamenta
            Text(
                text = temperamentList,
                style = TextStyle(fontStyle = FontStyle.Italic)
            )
        }
        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
        )
    }
}

private fun uzmiTri(
    data: Cat
): List<String> {
    val temperamentList = data.temperament.split(",")
    return temperamentList.shuffled().take(3)
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Preview
//@Composable
//fun PreviewCatListScreen() {
//    ProjekatTheme {
//        CatListScreen(
//            state = CatsListState(cats = SampleData),
//            onInsertClick = {},
//            onItemClick = {},
//        )
//    }
//}

