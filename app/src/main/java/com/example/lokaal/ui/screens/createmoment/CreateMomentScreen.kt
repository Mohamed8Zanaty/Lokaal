package com.example.lokaal.ui.screens.createmoment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.R
import com.example.lokaal.ui.screens.createmoment.components.LocationCard
import com.example.lokaal.ui.screens.createmoment.components.PhotoPreview
import com.example.lokaal.ui.screens.createmoment.components.PostButton
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CreateMomentScreen(
    photoBase64: String,
    onPostSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<CreateMomentViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchLocation(context)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onPostSuccess()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "New moment",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            
            PhotoPreview(
                photoBase64 = photoBase64,
                locationName = uiState.locationName
            )

            
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = "Caption",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = uiState.caption,
                    onValueChange = viewModel::updateCaption,
                    placeholder = {
                        Text(
                            text = "What's happening here?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            
            LocationCard(
                locationName = uiState.locationName,
                onRefresh = { viewModel.fetchLocation(context) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            
            PostButton(
                isLoading = uiState.isLoading,
                onClick = { viewModel.postMoment(context, photoBase64) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}