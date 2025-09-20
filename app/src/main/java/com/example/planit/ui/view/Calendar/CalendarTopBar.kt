package com.example.planit.ui.view.Calendar

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.planit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    selectedTab: String = "Schedule",
    onTabSelected: (String) -> Unit,
    onClose: () -> Unit,
    onAddEvent: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close Icon
                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null,
                        tint = Color(0xFFE88181)
                    )
                }
//                Spacer(modifier = Modifier.weight(1f))
                // Custom Segmented Control
                Row(
                    modifier = Modifier
                        .background(Color(0xFFFDE2E2), shape = RoundedCornerShape(50))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SegmentedButton(
                        text = "Schedule",
                        isSelected = selectedTab == "Schedule",
                        onClick = { onTabSelected("Schedule") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SegmentedButton(
                        text = "Reminder",
                        isSelected = selectedTab == "Reminder",
                        onClick = { onTabSelected("Reminder") }
                    )
                }
//                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onAddEvent) {
                    Icon(Icons.Default.Add, contentDescription = "Close", tint = Color(0xFFE88181))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun SegmentedButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFFFB3B3) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFFD9667B)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontSize = 20.sp)
    }
}