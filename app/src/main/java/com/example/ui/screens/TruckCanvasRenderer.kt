package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.data.models.CameraView
import com.example.data.models.CargoJob
import com.example.data.models.LiveryOption
import com.example.data.models.RouteLocation
import com.example.data.models.TelemetryState
import com.example.data.models.TrafficVehicle
import com.example.data.models.TruckModelConfig
import com.example.data.models.WeatherType
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TruckCanvasRenderer(
    telemetry: TelemetryState,
    truckModel: TruckModelConfig,
    livery: LiveryOption,
    route: RouteLocation,
    activeJob: CargoJob?,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.44f

        // 1. Draw Sky & Mountain Backdrop
        drawHimalayanSkyAndPeaks(w, h, horizonY, telemetry.weather, telemetry.steerInput, telemetry.distanceTraveledMeters)

        // 2. Draw 3D Perspective Mountain Highway
        drawMountainHighway(w, h, horizonY, telemetry)

        // 3. Draw Roadside Scenery (Trees, Dhabas, Indian Oil stations, Road Signs)
        drawRoadsideLandmarks(w, h, horizonY, telemetry)

        // 4. Draw Traffic Vehicles (JKSRTC Buses, Tata Sumo Taxis, Cars)
        drawTrafficVehicles(w, h, horizonY, telemetry)

        // 5. Draw Player Truck depending on Camera View
        when (telemetry.cameraView) {
            CameraView.THIRD_PERSON -> drawThirdPersonTruck(w, h, horizonY, telemetry, truckModel, livery, activeJob)
            CameraView.INTERIOR_COCKPIT -> drawCockpitView(w, h, horizonY, telemetry, livery)
            CameraView.BUMPER -> drawBumperView(w, h, horizonY, telemetry, livery)
        }

        // 6. Rain Weather Effect
        if (telemetry.weather == WeatherType.RAIN_STORM) {
            drawRainEffect(w, h, telemetry.distanceTraveledMeters)
        }
    }
}

private fun DrawScope.drawHimalayanSkyAndPeaks(
    w: Float,
    h: Float,
    horizonY: Float,
    weather: WeatherType,
    steerInput: Float,
    distMeters: Float
) {
    // Sky gradient
    val skyBrush = when (weather) {
        WeatherType.DAY_CLEAR -> Brush.verticalGradient(
            listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFFBAE6FD)),
            startY = 0f, endY = horizonY
        )
        WeatherType.SUNSET -> Brush.verticalGradient(
            listOf(Color(0xFF7C2D12), Color(0xFFEA580C), Color(0xFFFDBA74)),
            startY = 0f, endY = horizonY
        )
        WeatherType.NIGHT -> Brush.verticalGradient(
            listOf(Color(0xFF030712), Color(0xFF0B132B), Color(0xFF1E1B4B)),
            startY = 0f, endY = horizonY
        )
        WeatherType.RAIN_STORM -> Brush.verticalGradient(
            listOf(Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569)),
            startY = 0f, endY = horizonY
        )
    }

    drawRect(brush = skyBrush, topLeft = Offset(0f, 0f), size = Size(w, horizonY))

    // Sun / Moon in sky
    if (weather == WeatherType.DAY_CLEAR) {
        drawCircle(
            color = Color(0xFFFEF08A),
            radius = 28.dp.toPx(),
            center = Offset(w * 0.78f, horizonY * 0.35f)
        )
    } else if (weather == WeatherType.SUNSET) {
        drawCircle(
            color = Color(0xFFF97316),
            radius = 34.dp.toPx(),
            center = Offset(w * 0.5f, horizonY * 0.65f)
        )
    } else if (weather == WeatherType.NIGHT) {
        drawCircle(
            color = Color(0xFFF8FAFC),
            radius = 20.dp.toPx(),
            center = Offset(w * 0.82f, horizonY * 0.3f)
        )
    }

    // Snow-Capped Himalayan Mountain Peaks with Parallax Scroll
    val parallaxOffset = (steerInput * 45f) + (distMeters * 0.08f) % w
    val peakPath = Path()
    peakPath.moveTo(0f, horizonY)

    val numPeaks = 8
    val peakWidth = w / 4f
    for (i in -1..numPeaks) {
        val px = (i * peakWidth) - (parallaxOffset % peakWidth)
        val peakHeight = horizonY * (0.35f + ((i % 3) * 0.12f))
        peakPath.lineTo(px + peakWidth * 0.5f, peakHeight)
        peakPath.lineTo(px + peakWidth, horizonY)
    }
    peakPath.lineTo(w, horizonY)
    peakPath.close()

    // Mountain rock body
    val mountainColor = when (weather) {
        WeatherType.NIGHT -> Color(0xFF0F172A)
        WeatherType.SUNSET -> Color(0xFF431407)
        WeatherType.RAIN_STORM -> Color(0xFF1E293B)
        else -> Color(0xFF334155)
    }
    drawPath(peakPath, mountainColor)

    // Snow tops
    val snowColor = if (weather == WeatherType.NIGHT) Color(0xFF94A3B8) else Color(0xFFF8FAFC)
    for (i in -1..numPeaks) {
        val px = (i * peakWidth) - (parallaxOffset % peakWidth)
        val peakHeight = horizonY * (0.35f + ((i % 3) * 0.12f))
        val snowPath = Path().apply {
            moveTo(px + peakWidth * 0.5f, peakHeight)
            lineTo(px + peakWidth * 0.35f, peakHeight + 35.dp.toPx())
            lineTo(px + peakWidth * 0.5f, peakHeight + 25.dp.toPx())
            lineTo(px + peakWidth * 0.65f, peakHeight + 35.dp.toPx())
            close()
        }
        drawPath(snowPath, snowColor)
    }

    // Mountain Valleys / Grass hills lower layer
    val hillPath = Path()
    hillPath.moveTo(0f, horizonY)
    for (x in 0..w.toInt() step 50) {
        val y = horizonY - 12.dp.toPx() * sin((x + distMeters * 0.2f) * 0.015f)
        hillPath.lineTo(x.toFloat(), y)
    }
    hillPath.lineTo(w, horizonY)
    hillPath.close()
    val hillColor = if (weather == WeatherType.NIGHT) Color(0xFF064E3B) else Color(0xFF166534)
    drawPath(hillPath, hillColor)
}

private fun DrawScope.drawMountainHighway(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState
) {
    val roadTopWidth = w * 0.14f
    val roadBottomWidth = w * 0.88f
    val curveShift = telemetry.steerInput * 60f

    // Ground grass on left and right
    val groundColor = if (telemetry.weather == WeatherType.NIGHT) Color(0xFF0F172A) else Color(0xFF14532D)
    drawRect(color = groundColor, topLeft = Offset(0f, horizonY), size = Size(w, h - horizonY))

    // Asphalt Trapezoid
    val roadPath = Path().apply {
        moveTo(w / 2f - roadTopWidth / 2f + curveShift, horizonY)
        lineTo(w / 2f + roadTopWidth / 2f + curveShift, horizonY)
        lineTo(w / 2f + roadBottomWidth / 2f, h)
        lineTo(w / 2f - roadBottomWidth / 2f, h)
        close()
    }
    drawPath(roadPath, Color(0xFF27272A))

    // Headlight cone on road asphalt in Night or Rain
    if (telemetry.weather == WeatherType.NIGHT || telemetry.weather == WeatherType.RAIN_STORM) {
        val lightCone = Path().apply {
            moveTo(w / 2f - 20.dp.toPx(), h * 0.72f)
            lineTo(w / 2f + 20.dp.toPx(), h * 0.72f)
            lineTo(w / 2f + roadBottomWidth * 0.45f, h)
            lineTo(w / 2f - roadBottomWidth * 0.45f, h)
            close()
        }
        drawPath(lightCone, Color(0x33FEF08A))
    }

    // White outer road edge lines
    drawLine(
        color = Color.White,
        start = Offset(w / 2f - roadTopWidth / 2f + curveShift, horizonY),
        end = Offset(w / 2f - roadBottomWidth / 2f, h),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White,
        start = Offset(w / 2f + roadTopWidth / 2f + curveShift, horizonY),
        end = Offset(w / 2f + roadBottomWidth / 2f, h),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Dashed Yellow Center Line (moving backwards with distance)
    val numDashes = 10
    val dashSpeed = (telemetry.distanceTraveledMeters * 1.5f) % 60f
    for (i in 0 until numDashes) {
        val t1 = (i.toFloat() + (dashSpeed / 60f)) / numDashes.toFloat()
        val t2 = ((i.toFloat() + 0.5f) + (dashSpeed / 60f)) / numDashes.toFloat()
        if (t1 in 0f..1f && t2 in 0f..1f) {
            val y1 = horizonY + (h - horizonY) * (t1 * t1)
            val y2 = horizonY + (h - horizonY) * (t2 * t2)
            val x1 = (w / 2f + curveShift * (1f - t1))
            val x2 = (w / 2f + curveShift * (1f - t2))
            val strokeW = (2f + (t1 * 6f)).dp.toPx()

            drawLine(
                color = Color(0xFFFACC15),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawRoadsideLandmarks(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState
) {
    val roadDist = telemetry.distanceTraveledMeters
    val cycleLength = 320f
    val relCycle = roadDist % cycleLength

    // Left Pine Trees repeating along the route
    for (i in 0..4) {
        val treeZ = (i * 70f - (relCycle * 0.6f) + cycleLength) % cycleLength
        val t = (treeZ / cycleLength).coerceIn(0f, 1f)
        val treeY = horizonY + (h - horizonY) * (t * t)
        val treeX = (w * 0.12f) * (1f - t * 0.6f)
        val treeSize = (14f + t * 40f).dp.toPx()

        drawPineTree(treeX, treeY, treeSize, telemetry.weather)
    }

    // Right Autumn Chinar Trees
    for (i in 0..4) {
        val treeZ = (i * 70f + 35f - (relCycle * 0.6f) + cycleLength) % cycleLength
        val t = (treeZ / cycleLength).coerceIn(0f, 1f)
        val treeY = horizonY + (h - horizonY) * (t * t)
        val treeX = w - ((w * 0.12f) * (1f - t * 0.6f))
        val treeSize = (16f + t * 44f).dp.toPx()

        drawChinarTree(treeX, treeY, treeSize, telemetry.weather)
    }

    // Highway Landmark Billboard (Sher-e-Kashmir Dhaba / Indian Oil pump)
    val landmarkCycle = roadDist % 800f
    if (landmarkCycle < 350f) {
        val t = (landmarkCycle / 350f).coerceIn(0f, 1f)
        val ly = horizonY + (h - horizonY) * (t * t)
        val lx = w * 0.88f
        val lw = (20f + t * 65f).dp.toPx()
        val lh = (15f + t * 45f).dp.toPx()

        // Dhaba Building
        drawRect(
            color = Color(0xFF334155),
            topLeft = Offset(lx, ly - lh),
            size = Size(lw, lh)
        )
        // Dhaba Roof
        val roofPath = Path().apply {
            moveTo(lx - 5f, ly - lh)
            lineTo(lx + lw / 2f, ly - lh - 12.dp.toPx() * t)
            lineTo(lx + lw + 5f, ly - lh)
            close()
        }
        drawPath(roofPath, Color(0xFFDC2626))
    }
}

private fun DrawScope.drawPineTree(x: Float, y: Float, size: Float, weather: WeatherType) {
    // Trunk
    val trunkW = size * 0.14f
    val trunkH = size * 0.35f
    drawRect(
        color = Color(0xFF451A03),
        topLeft = Offset(x - trunkW / 2f, y - trunkH),
        size = Size(trunkW, trunkH)
    )

    // Conical pine tiers
    val foliageColor = if (weather == WeatherType.NIGHT) Color(0xFF064E3B) else Color(0xFF14532D)
    val t1 = Path().apply {
        moveTo(x, y - size)
        lineTo(x - size * 0.42f, y - trunkH)
        lineTo(x + size * 0.42f, y - trunkH)
        close()
    }
    drawPath(t1, foliageColor)
}

private fun DrawScope.drawChinarTree(x: Float, y: Float, size: Float, weather: WeatherType) {
    // Trunk
    val trunkW = size * 0.16f
    val trunkH = size * 0.4f
    drawRect(
        color = Color(0xFF573A27),
        topLeft = Offset(x - trunkW / 2f, y - trunkH),
        size = Size(trunkW, trunkH)
    )

    // Golden Chinar autumn foliage
    val chinarColor = if (weather == WeatherType.NIGHT) Color(0xFF9A3412) else Color(0xFFD97706)
    drawCircle(
        color = chinarColor,
        radius = size * 0.36f,
        center = Offset(x, y - trunkH - size * 0.28f)
    )
}

private fun DrawScope.drawTrafficVehicles(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState
) {
    telemetry.trafficList.forEach { v ->
        val relZ = v.relZ
        // Map relZ (-220 to 30) into 0..1 perspective
        if (relZ in -220f..40f) {
            val normZ = ((relZ + 220f) / 260f).coerceIn(0f, 1f)
            val ty = horizonY + (h - horizonY) * (normZ * normZ)
            val curveOffset = telemetry.steerInput * 40f * (1f - normZ)
            val tx = (w / 2f) + (v.relX * (18f + normZ * 85f)) + curveOffset

            val scale = (0.35f + normZ * 0.9f)
            when (v.type) {
                "bus" -> drawTrafficBus(tx, ty, scale, telemetry.weather)
                "taxi" -> drawTrafficTaxi(tx, ty, scale, telemetry.weather)
                else -> drawTrafficCar(tx, ty, scale, telemetry.weather)
            }
        }
    }
}

private fun DrawScope.drawTrafficBus(x: Float, y: Float, scale: Float, weather: WeatherType) {
    val bw = 48.dp.toPx() * scale
    val bh = 36.dp.toPx() * scale

    // JKSRTC Himalayan Express Blue Coach
    drawRoundRect(
        color = Color(0xFF1D4ED8),
        topLeft = Offset(x - bw / 2f, y - bh),
        size = Size(bw, bh),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx() * scale, 4.dp.toPx() * scale)
    )

    // White stripe
    drawRect(
        color = Color.White,
        topLeft = Offset(x - bw / 2f, y - bh * 0.55f),
        size = Size(bw, bh * 0.15f)
    )

    // Taillights
    val lightSize = 5.dp.toPx() * scale
    drawRect(color = Color(0xFFEF4444), topLeft = Offset(x - bw / 2f + 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
    drawRect(color = Color(0xFFEF4444), topLeft = Offset(x + bw / 2f - lightSize - 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
}

private fun DrawScope.drawTrafficTaxi(x: Float, y: Float, scale: Float, weather: WeatherType) {
    val tw = 36.dp.toPx() * scale
    val th = 24.dp.toPx() * scale

    // Tata Sumo mountain taxi (White body with yellow stripe)
    drawRoundRect(
        color = Color(0xFFF8FAFC),
        topLeft = Offset(x - tw / 2f, y - th),
        size = Size(tw, th),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx() * scale, 3.dp.toPx() * scale)
    )
    drawRect(
        color = Color(0xFFEAB308),
        topLeft = Offset(x - tw / 2f, y - th * 0.45f),
        size = Size(tw, th * 0.18f)
    )
    // Red Taillights
    val lightSize = 4.dp.toPx() * scale
    drawRect(color = Color(0xFFDC2626), topLeft = Offset(x - tw / 2f + 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
    drawRect(color = Color(0xFFDC2626), topLeft = Offset(x + tw / 2f - lightSize - 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
}

private fun DrawScope.drawTrafficCar(x: Float, y: Float, scale: Float, weather: WeatherType) {
    val cw = 32.dp.toPx() * scale
    val ch = 20.dp.toPx() * scale

    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(x - cw / 2f, y - ch),
        size = Size(cw, ch),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx() * scale, 3.dp.toPx() * scale)
    )
    val lightSize = 4.dp.toPx() * scale
    drawRect(color = Color(0xFFDC2626), topLeft = Offset(x - cw / 2f + 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
    drawRect(color = Color(0xFFDC2626), topLeft = Offset(x + cw / 2f - lightSize - 2f, y - lightSize - 2f), size = Size(lightSize, lightSize))
}

private fun DrawScope.drawThirdPersonTruck(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState,
    truckModel: TruckModelConfig,
    livery: LiveryOption,
    activeJob: CargoJob?
) {
    val truckCenter = Offset(w * 0.5f, h * 0.76f)
    val rollDeg = telemetry.bodyRollAngle // Tilts with Pahadi Sway

    // Draw Underglow Neon if enabled
    if (telemetry.stroboActive) {
        drawOval(
            color = Color(0xFF3B82F6).copy(alpha = 0.55f),
            topLeft = Offset(truckCenter.x - 70.dp.toPx(), truckCenter.y + 24.dp.toPx()),
            size = Size(140.dp.toPx(), 22.dp.toPx())
        )
    }

    // Truck Body with Rotation (Pahadi Sway tilt)
    val nativeCanvas = drawContext.canvas.nativeCanvas
    nativeCanvas.save()
    nativeCanvas.rotate(rollDeg, truckCenter.x, truckCenter.y)

    val truckW = 124.dp.toPx()
    val truckH = 88.dp.toPx()
    val cabLeft = truckCenter.x - truckW / 2f
    val cabTop = truckCenter.y - truckH

    // 1. Truck Bed & Cargo Body
    val primaryColor = Color(livery.primaryColorHex)
    val secondaryColor = Color(livery.secondaryColorHex)
    val tarpaulinColor = Color(livery.tarpaulinColorHex)

    // Wooden / Steel cargo bed
    drawRoundRect(
        color = primaryColor,
        topLeft = Offset(cabLeft, cabTop),
        size = Size(truckW, truckH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
    )

    // Tarpaulin or Timber cargo on top
    val cargoH = 48.dp.toPx()
    if (activeJob?.id == "job_deodar_timber") {
        // Giant logs
        drawRoundRect(color = Color(0xFF45220A), topLeft = Offset(cabLeft + 8.dp.toPx(), cabTop - cargoH + 10.dp.toPx()), size = Size(truckW - 16.dp.toPx(), 20.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx()))
        drawRoundRect(color = Color(0xFF45220A), topLeft = Offset(cabLeft + 14.dp.toPx(), cabTop - cargoH - 5.dp.toPx()), size = Size(truckW - 28.dp.toPx(), 20.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx()))
    } else if (activeJob?.id == "job_petroleum_tanker") {
        // Stainless steel cylinder
        drawRoundRect(color = Color(0xFFCBD5E1), topLeft = Offset(cabLeft + 6.dp.toPx(), cabTop - cargoH), size = Size(truckW - 12.dp.toPx(), cargoH), cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()))
        drawRect(color = Color(0xFFEF4444), topLeft = Offset(truckCenter.x - 14.dp.toPx(), cabTop - cargoH + 14.dp.toPx()), size = Size(28.dp.toPx(), 18.dp.toPx()))
    } else {
        // High Tarpaulin cover (Apple crates / sacks)
        drawRoundRect(
            color = tarpaulinColor,
            topLeft = Offset(cabLeft + 4.dp.toPx(), cabTop - cargoH),
            size = Size(truckW - 8.dp.toPx(), cargoH + 6.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
    }

    // Cab rear window
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(truckCenter.x - 24.dp.toPx(), cabTop + 14.dp.toPx()),
        size = Size(48.dp.toPx(), 24.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    // Kashmiri Truck Art Slogan banner on rear
    val bannerText = livery.quoteText
    drawRect(
        color = secondaryColor,
        topLeft = Offset(cabLeft + 8.dp.toPx(), cabTop + truckH * 0.58f),
        size = Size(truckW - 16.dp.toPx(), 16.dp.toPx())
    )
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 9.dp.toPx()
        textAlign = android.graphics.Paint.Align.CENTER
        isFakeBoldText = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText(
        bannerText,
        truckCenter.x,
        cabTop + truckH * 0.58f + 12.dp.toPx(),
        textPaint
    )

    // Mudflaps with "JANNAT-E-KASHMIR" / "HORN PLEASE"
    val flapW = 34.dp.toPx()
    val flapH = 22.dp.toPx()
    drawRect(color = Color(0xFF09090B), topLeft = Offset(cabLeft + 6.dp.toPx(), cabTop + truckH), size = Size(flapW, flapH))
    drawRect(color = Color(0xFF09090B), topLeft = Offset(cabLeft + truckW - flapW - 6.dp.toPx(), cabTop + truckH), size = Size(flapW, flapH))
    drawRect(color = Color(0xFFFACC15), topLeft = Offset(cabLeft + 6.dp.toPx(), cabTop + truckH + flapH - 4.dp.toPx()), size = Size(flapW, 4.dp.toPx()))
    drawRect(color = Color(0xFFFACC15), topLeft = Offset(cabLeft + truckW - flapW - 6.dp.toPx(), cabTop + truckH + flapH - 4.dp.toPx()), size = Size(flapW, 4.dp.toPx()))

    // Taillights & Brake glow
    val isBraking = telemetry.brakeInput > 0.1f || telemetry.isHandbrakeActive
    val tailColor = if (isBraking) Color(0xFFEF4444) else Color(0xFF991B1B)
    drawRect(color = tailColor, topLeft = Offset(cabLeft + 12.dp.toPx(), cabTop + truckH - 12.dp.toPx()), size = Size(20.dp.toPx(), 8.dp.toPx()))
    drawRect(color = tailColor, topLeft = Offset(cabLeft + truckW - 32.dp.toPx(), cabTop + truckH - 12.dp.toPx()), size = Size(20.dp.toPx(), 8.dp.toPx()))

    // Turn signal flashers
    if (telemetry.turnSignalBlink) {
        if (telemetry.turnSignal == "left" || telemetry.turnSignal == "hazard") {
            drawCircle(color = Color(0xFFF59E0B), radius = 6.dp.toPx(), center = Offset(cabLeft + 6.dp.toPx(), cabTop + truckH - 8.dp.toPx()))
        }
        if (telemetry.turnSignal == "right" || telemetry.turnSignal == "hazard") {
            drawCircle(color = Color(0xFFF59E0B), radius = 6.dp.toPx(), center = Offset(cabLeft + truckW - 6.dp.toPx(), cabTop + truckH - 8.dp.toPx()))
        }
    }

    // Top Strobe lightbar LEDs
    if (telemetry.stroboActive) {
        val ledW = 12.dp.toPx()
        for (i in 0..4) {
            val ledColor = if (i % 2 == 0) Color(0xFF3B82F6) else Color(0xFFEF4444)
            drawRect(
                color = ledColor,
                topLeft = Offset(cabLeft + 22.dp.toPx() + i * 16.dp.toPx(), cabTop - cargoH - 4.dp.toPx()),
                size = Size(ledW, 5.dp.toPx())
            )
        }
    }

    // Dual Rear Tires
    val tireW = 14.dp.toPx()
    val tireH = 24.dp.toPx()
    drawRoundRect(color = Color(0xFF18181B), topLeft = Offset(cabLeft + 10.dp.toPx(), cabTop + truckH - 8.dp.toPx()), size = Size(tireW, tireH), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()))
    drawRoundRect(color = Color(0xFF18181B), topLeft = Offset(cabLeft + truckW - 24.dp.toPx(), cabTop + truckH - 8.dp.toPx()), size = Size(tireW, tireH), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()))

    nativeCanvas.restore()
}

private fun DrawScope.drawCockpitView(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState,
    livery: LiveryOption
) {
    // Windshield frame & pillars
    val pillarW = 28.dp.toPx()
    drawRect(color = Color(0xFF18181B), topLeft = Offset(0f, 0f), size = Size(pillarW, h))
    drawRect(color = Color(0xFF18181B), topLeft = Offset(w - pillarW, 0f), size = Size(pillarW, h))

    // Top sunvisor banner with "SHER-E-KASHMIR" / windshield text
    val visorH = 34.dp.toPx()
    drawRect(color = Color(0xFF09090B), topLeft = Offset(pillarW, 0f), size = Size(w - pillarW * 2, visorH))
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#FACC15")
        textSize = 14.dp.toPx()
        textAlign = android.graphics.Paint.Align.CENTER
        isFakeBoldText = true
        typeface = android.graphics.Typeface.MONOSPACE
    }
    drawContext.canvas.nativeCanvas.drawText(
        livery.windshieldBanner,
        w / 2f,
        visorH * 0.7f,
        textPaint
    )

    // Center rearview mirror
    val mirrorW = 90.dp.toPx()
    val mirrorH = 32.dp.toPx()
    drawRoundRect(
        color = Color(0xFF27272A),
        topLeft = Offset(w / 2f - mirrorW / 2f, visorH),
        size = Size(mirrorW, mirrorH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
    )
    drawRect(
        color = Color(0xFF475569),
        topLeft = Offset(w / 2f - mirrorW / 2f + 3f, visorH + 3f),
        size = Size(mirrorW - 6f, mirrorH - 6f)
    )

    // Hanging Kashmiri lucky charm from rearview mirror
    val charmX = w / 2f - (telemetry.bodyRollAngle * 1.5f)
    drawLine(
        color = Color.White,
        start = Offset(w / 2f, visorH + mirrorH),
        end = Offset(charmX, visorH + mirrorH + 28.dp.toPx()),
        strokeWidth = 1.5.dp.toPx()
    )
    drawCircle(
        color = Color(0xFF16A34A),
        radius = 8.dp.toPx(),
        center = Offset(charmX, visorH + mirrorH + 36.dp.toPx())
    )

    // Windshield Wipers Animation during monsoon
    if (telemetry.wipersActive) {
        val sweepAngle = sin(telemetry.distanceTraveledMeters * 0.15f) * 0.7f
        val wiperBase = Offset(w * 0.35f, h * 0.85f)
        val wiperLen = h * 0.38f
        drawLine(
            color = Color(0xFF09090B),
            start = wiperBase,
            end = Offset(wiperBase.x + cos(sweepAngle - PI.toFloat() / 2f) * wiperLen, wiperBase.y + sin(sweepAngle - PI.toFloat() / 2f) * wiperLen),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Dashboard console ledge at bottom
    val dashH = h * 0.22f
    drawRect(
        color = Color(0xFF18181B),
        topLeft = Offset(0f, h - dashH),
        size = Size(w, dashH)
    )
}

private fun DrawScope.drawBumperView(
    w: Float,
    h: Float,
    horizonY: Float,
    telemetry: TelemetryState,
    livery: LiveryOption
) {
    // Low-slung front chrome bumper visible at screen bottom
    val bH = 26.dp.toPx()
    drawRect(color = Color(0xFF27272A), topLeft = Offset(0f, h - bH), size = Size(w, bH))
    // Front license plate: "JK 01 A 7860"
    val plateW = 100.dp.toPx()
    val plateH = 20.dp.toPx()
    drawRect(color = Color(0xFFFACC15), topLeft = Offset(w / 2f - plateW / 2f, h - bH + 3.dp.toPx()), size = Size(plateW, plateH))
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 11.dp.toPx()
        textAlign = android.graphics.Paint.Align.CENTER
        isFakeBoldText = true
        typeface = android.graphics.Typeface.MONOSPACE
    }
    drawContext.canvas.nativeCanvas.drawText("JK 01 A 7860", w / 2f, h - bH + 16.dp.toPx(), textPaint)
}

private fun DrawScope.drawRainEffect(w: Float, h: Float, distMeters: Float) {
    val numDrops = 90
    for (i in 0 until numDrops) {
        val seed = i * 137.5f
        val rx = (seed * 97f + distMeters * 3f) % w
        val ry = (seed * 63f + distMeters * 8f) % h
        drawLine(
            color = Color(0x9993C5FD),
            start = Offset(rx, ry),
            end = Offset(rx - 8.dp.toPx(), ry + 16.dp.toPx()),
            strokeWidth = 1.2.dp.toPx()
        )
    }
}
