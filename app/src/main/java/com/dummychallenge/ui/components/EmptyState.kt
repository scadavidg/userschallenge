package com.dummychallenge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dummychallenge.ui.theme.DummyTheme

@Composable
fun EmptyState(
    title: String = "No hay usuarios",
    message: String = "Aún no tienes usuarios registrados. ¡Crea tu primer usuario para comenzar!",
    buttonText: String = "Crear Usuario",
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Empty State",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onActionClick
            ) {
                Text(buttonText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    DummyTheme {
        EmptyState(
            onActionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateCustomPreview() {
    DummyTheme {
        EmptyState(
            title = "Sin resultados",
            message = "No se encontraron usuarios que coincidan con tu búsqueda. Intenta con otros términos.",
            buttonText = "Limpiar búsqueda",
            onActionClick = {}
        )
    }
}
