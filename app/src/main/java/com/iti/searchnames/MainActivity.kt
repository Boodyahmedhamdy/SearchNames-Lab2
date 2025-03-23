package com.iti.searchnames

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iti.searchnames.ui.theme.SearchNamesTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private val names = listOf("boody", "ahmed", "hamdy", "hassan")
    private val filteredNames = MutableStateFlow(names)
    private val searchQuery = MutableSharedFlow<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SearchNamesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var query by remember { mutableStateOf("")}
                    val scope = rememberCoroutineScope()
                    val namesState = filteredNames.collectAsState()

                    LaunchedEffect(query) {
                        searchQuery
                            .collect {value ->
                                Log.i(TAG, "onCreate: filtered names: ${filteredNames.value}")
                                filteredNames.value = names.filter { it.contains(value) }
                                Log.i(TAG, "onCreate: filtered names: ${filteredNames.value}")
                            }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        OutlinedTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                scope.launch {
                                    searchQuery.emit(query)
                                    Log.i(TAG, "onCreate: emitted $query")
                                }
                            }
                        )

                        LazyColumn {
                            items(namesState.value) {
                                Text(text = it)
                            }
                        }

                    }
                }
            }
        }
    }
}
