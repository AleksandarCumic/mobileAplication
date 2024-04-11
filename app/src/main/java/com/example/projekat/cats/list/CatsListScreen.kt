package com.example.projekat.cats.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.repository.SampleData
import com.example.projekat.core.theme.ProjekatTheme

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
        onSearch = { searchText ->
            catListViewModel.searchCatsByName(searchText)
        },
        resetSearch = {
            catListViewModel.clearSearch()
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun CatListScreen(
    state: CatsListState?,
    onItemClick: (Cat) -> Unit,
    onSearch: (String) -> Unit,
    resetSearch: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "List of Cats") },
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Segment pretrage je sada ispod naslova
                        .padding(bottom = 22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = { Text("Search") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    if (searchText.isNotBlank()) {
                                        onSearch(searchText)
                                    } else {
                                        searchText = ""
                                        resetSearch()
                                    }
                                }
                            ),
                        )
                        IconButton(onClick = {
                            if (searchText.isNotBlank()) {
                                onSearch(searchText)
                            } else {
                                searchText = ""
                                resetSearch()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                            )
                        }
                    }
                }
            }
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
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
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
                    if (data.alternativeNames.isNotEmpty()) {
                        append(" (${data.alternativeNames})")
                    }
                },
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.description.take(250).plus("..."),
                style = TextStyle(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val temperamentList = uzmiTri(data).joinToString(", ")
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

private fun uzmiTri(data: Cat): List<String> {
    val temperamentList = data.temperament.split(",")
    return temperamentList.shuffled().take(3)
}
