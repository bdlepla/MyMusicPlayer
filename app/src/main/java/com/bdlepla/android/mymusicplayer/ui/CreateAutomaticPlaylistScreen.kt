package com.bdlepla.android.mymusicplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.AutomaticPlaylistConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAutomaticPlaylistScreen(
    initialConfig: AutomaticPlaylistConfig? = null,
    onSave: (AutomaticPlaylistConfig) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(initialConfig?.name ?: "") }
    var source by remember { mutableStateOf(initialConfig?.source?.takeIf { it.isNotEmpty() } ?: "All Songs") }
    var sourceExpanded by remember { mutableStateOf(false) }

    var applyFilter by remember { mutableStateOf(initialConfig?.applyFilter ?: true) }
    var attribute by remember { mutableStateOf(initialConfig?.attribute?.takeIf { it.isNotEmpty() } ?: "Favorite") }
    var attributeExpanded by remember { mutableStateOf(false) }

    var value by remember { mutableStateOf(initialConfig?.value?.takeIf { it.isNotEmpty() } ?: "True") }
    var valueExpanded by remember { mutableStateOf(false) }

    var applyLimit by remember { mutableStateOf(initialConfig?.applyLimit ?: false) }
    var limitType by remember { mutableStateOf(initialConfig?.limitType?.takeIf { it.isNotEmpty() } ?: "Number of songs") }
    var limitTypeExpanded by remember { mutableStateOf(false) }
    var limitValueStr by remember { mutableStateOf(initialConfig?.let { if (it.limitValue > 0) it.limitValue.toString() else "10" } ?: "10") }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = if (initialConfig != null) "Edit Automatic Playlist" else "Create Automatic Playlist",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Section 1: Name
                Text("1. Playlist Name", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(12.dp))

                // Section 2: Source list of songs
                Text("2. Source List", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(4.dp))
                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = !sourceExpanded }
                ) {
                    OutlinedTextField(
                        value = source,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Source") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Songs") },
                            onClick = {
                                source = "All Songs"
                                sourceExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(12.dp))

                // Section 3: Filtering Criteria
                Text("3. Filtering Criteria", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = applyFilter,
                        onCheckedChange = { applyFilter = it }
                    )
                    Text("Apply Filter Criteria")
                }
                if (applyFilter) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Attribute dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = attributeExpanded,
                                onExpandedChange = { attributeExpanded = !attributeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = attribute,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Attribute") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = attributeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = attributeExpanded,
                                    onDismissRequest = { attributeExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Favorite") },
                                        onClick = {
                                            attribute = "Favorite"
                                            attributeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("is")
                        Spacer(modifier = Modifier.width(8.dp))
                        // Value dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = valueExpanded,
                                onExpandedChange = { valueExpanded = !valueExpanded }
                            ) {
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Value") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = valueExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = valueExpanded,
                                    onDismissRequest = { valueExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("True") },
                                        onClick = {
                                            value = "True"
                                            valueExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("False") },
                                        onClick = {
                                            value = "False"
                                            valueExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(12.dp))

                // Section 4: Limiting Results
                Text("4. Limit Results", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = applyLimit,
                        onCheckedChange = { applyLimit = it }
                    )
                    Text("Apply Limit")
                }
                if (applyLimit) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = limitTypeExpanded,
                                onExpandedChange = { limitTypeExpanded = !limitTypeExpanded }
                            ) {
                                OutlinedTextField(
                                    value = limitType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Limit Type") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = limitTypeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = limitTypeExpanded,
                                    onDismissRequest = { limitTypeExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Number of songs") },
                                        onClick = {
                                            limitType = "Number of songs"
                                            limitTypeExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Minutes") },
                                        onClick = {
                                            limitType = "Minutes"
                                            limitTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = limitValueStr,
                            onValueChange = { limitValueStr = it },
                            label = { Text("Limit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(24.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val limitVal = limitValueStr.toIntOrNull() ?: 10
                            val config = AutomaticPlaylistConfig.newBuilder()
                                .setName(name.trim())
                                .setSource(source)
                                .setApplyFilter(applyFilter)
                                .setAttribute(attribute)
                                .setValue(value)
                                .setApplyLimit(applyLimit)
                                .setLimitType(limitType)
                                .setLimitValue(limitVal)
                                .build()
                            onSave(config)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (initialConfig != null) "Update Automatic Playlist" else "Save Automatic Playlist")
                }
            }
        }
    )
}
