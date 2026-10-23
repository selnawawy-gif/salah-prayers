package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SalahViewModel
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    viewModel: SalahViewModel,
    modifier: Modifier = Modifier
) {
    val heading by viewModel.compassHeading.collectAsState()
    val qiblaBearing by viewModel.qiblaBearing.collectAsState()
    val distanceKm by viewModel.distanceToKaabaKm.collectAsState()
    val city by viewModel.prefs.userCity.collectAsState()
    val isSensorAvailable by viewModel.isCompassAvailable.collectAsState()

    // Difference between user heading and Qibla bearing
    val diff = remember(heading, qiblaBearing) {
        var d = abs(heading - qiblaBearing)
        if (d > 180f) d = 360f - d
        d
    }
    val isAligned = diff <= 3.5f

    // Smooth needle rotation: relative angle from phone's top
    val relativeQiblaAngle = (qiblaBearing - heading + 360f) % 360f
    val animatedRelativeAngle by animateFloatAsState(
        targetValue = relativeQiblaAngle,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "qibla_angle"
    )

    Scaffold(
        modifier = modifier.testTag("qibla_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Qibla Compass",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Alignment Status Banner
            AnimatedVisibility(
                visible = isAligned,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🕋",
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aligned with Kaaba! Face this direction.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            if (!isAligned) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Rotate until the golden Kaaba needle points straight UP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Compass Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isAligned) listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            ) else listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        width = if (isAligned) 4.dp else 2.dp,
                        color = if (isAligned) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                    .testTag("compass_dial"),
                contentAlignment = Alignment.Center
            ) {
                // Compass Dial Background & Cardinal Marks
                CompassDialTicks(heading = heading)

                // Rotating Qibla Needle pointing directly to Kaaba
                QiblaNeedle(
                    relativeAngle = animatedRelativeAngle,
                    isAligned = isAligned
                )

                // Center hub
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isAligned) Color(0xFF10B981) else MaterialTheme.colorScheme.primary)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🕋",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Readouts: Heading, Qibla Bearing, Distance
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CURRENT HEADING",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${heading.roundToInt()}°",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "QIBLA BEARING",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${qiblaBearing.roundToInt()}°",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "MECCA DISTANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "${distanceKm} km",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Calibration Tip / Sensor Note
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSensorAvailable) "Keep phone level away from metal or magnets."
                    else "Compass sensors unavailable. Using GPS calculations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CompassDialTicks(heading: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.width / 2f) - 16.dp.toPx()

        for (i in 0 until 360 step 15) {
            val angleDeg = i - heading - 90f
            val angleRad = Math.toRadians(angleDeg.toDouble())

            val tickLength = if (i % 90 == 0) 18.dp.toPx() else if (i % 45 == 0) 12.dp.toPx() else 6.dp.toPx()
            val strokeWidth = if (i % 90 == 0) 3.dp.toPx() else if (i % 45 == 0) 2.dp.toPx() else 1.dp.toPx()
            val color = if (i == 0) Color(0xFFEF4444) else if (i % 90 == 0) primaryColor else outlineColor

            val startX = (center.x + (radius - tickLength) * cos(angleRad)).toFloat()
            val startY = (center.y + (radius - tickLength) * sin(angleRad)).toFloat()
            val endX = (center.x + radius * cos(angleRad)).toFloat()
            val endY = (center.y + radius * sin(angleRad)).toFloat()

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun QiblaNeedle(
    relativeAngle: Float,
    isAligned: Boolean
) {
    val needleColor = if (isAligned) Color(0xFF10B981) else Color(0xFFD4AF37)
    val shadowColor = if (isAligned) Color(0xFF047857) else Color(0xFF9E7C0C)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(relativeAngle)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val needleLength = (size.width / 2f) - 34.dp.toPx()

        // Needle Path pointing up to Kaaba
        val path = Path().apply {
            moveTo(center.x, center.y - needleLength) // Tip
            lineTo(center.x - 14.dp.toPx(), center.y - 10.dp.toPx())
            lineTo(center.x, center.y)
            close()
        }

        val rightPath = Path().apply {
            moveTo(center.x, center.y - needleLength)
            lineTo(center.x + 14.dp.toPx(), center.y - 10.dp.toPx())
            lineTo(center.x, center.y)
            close()
        }

        drawPath(path, needleColor)
        drawPath(rightPath, shadowColor)

        // Tail counter-needle
        val tailPath = Path().apply {
            moveTo(center.x, center.y + 40.dp.toPx())
            lineTo(center.x - 8.dp.toPx(), center.y)
            lineTo(center.x + 8.dp.toPx(), center.y)
            close()
        }
        drawPath(tailPath, Color.Gray.copy(alpha = 0.5f))
    }
}
