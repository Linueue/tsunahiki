package com.kldevs.tsunahiki.game.character

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser
import com.kldevs.tsunahiki.game.utils.CanvasStroke
import tsunahiki.shared.generated.resources.Res
import kotlin.math.hypot

private fun extractViewBox(svg: String): Rect {
    val regex = """viewBox\s*=\s*["']([^"']+)["']""".toRegex()
    val match = regex.find(svg)!!
    val parts = match.groupValues[1]
        .split(' ')
        .mapNotNull { it.toFloatOrNull() }
    return Rect(parts[0], parts[1], parts[0] + parts[2], parts[1] + parts[3])
}

private fun extractPathData(svg: String): List<String> {
    val regex = """\bd\s*=\s*["']([^"']+)["']""".toRegex()

    return regex.findAll(svg).map { r ->
        r.groupValues[1]
    }.toList()
}

private fun svgToPoints(path: String, points: Int = 10): List<Offset> {
    val nodes = PathParser().parsePathString(path).toNodes()

    return pathNodesToPoints(nodes, points)
}

suspend fun parseSVG(filepath: String): List<CanvasStroke> {
    val text = Res.readBytes(filepath).decodeToString()
    val paths = extractPathData(text)
    val viewBox = extractViewBox(text)

    val strokes = paths.map { path ->
        val offsets = svgToPoints(path)

        offsets
    }

    val centered = strokes.map { stroke ->
        stroke.map {
            val normalized = Offset(it.x / viewBox.width, it.y / viewBox.height)
            val ranged = normalized * 2.0f

            Offset(ranged.x - 1.0f, ranged.y - 1.0f)
        }
    }

    return centered.map { stroke ->
        CanvasStroke(stroke.toMutableList(), Color.Black)
    }
}

private fun pathNodesToPoints(
    nodes: List<PathNode>,
    samplesPerSegment: Int = 20
): List<Offset> {
    val points = mutableListOf<Offset>()
    var current = Offset.Zero
    var start = Offset.Zero

    for (node in nodes) {
        when (node) {
            is PathNode.MoveTo -> {
                current = Offset(node.x, node.y)
                start = current
                points.add(current)
            }
            is PathNode.RelativeMoveTo -> {
                current = current + Offset(node.dx, node.dy)
                start = current
                points.add(current)
            }
            is PathNode.LineTo -> {
                val end = Offset(node.x, node.y)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.RelativeLineTo -> {
                val end = current + Offset(node.dx, node.dy)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.HorizontalTo -> {
                val end = Offset(node.x, current.y)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.RelativeHorizontalTo -> {
                val end = Offset(current.x + node.dx, current.y)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.VerticalTo -> {
                val end = Offset(current.x, node.y)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.RelativeVerticalTo -> {
                val end = Offset(current.x, current.y + node.dy)
                points.addAll(sampleLine(current, end, samplesPerSegment))
                current = end
            }
            is PathNode.CurveTo -> {
                val control1 = Offset(node.x1, node.y1)
                val control2 = Offset(node.x2, node.y2)
                val end = Offset(node.x3, node.y3)
                points.addAll(sampleCubicBezier(current, control1, control2, end, samplesPerSegment))
                current = end
            }
            is PathNode.RelativeCurveTo -> {
                val control1 = current + Offset(node.dx1, node.dy1)
                val control2 = current + Offset(node.dx2, node.dy2)
                val end = current + Offset(node.dx3, node.dy3)
                points.addAll(sampleCubicBezier(current, control1, control2, end, samplesPerSegment))
                current = end
            }
            is PathNode.QuadTo -> {
                val control = Offset(node.x1, node.y1)
                val end = Offset(node.x2, node.y2)
                points.addAll(sampleQuadraticBezier(current, control, end, samplesPerSegment))
                current = end
            }
            is PathNode.RelativeQuadTo -> {
                val control = current + Offset(node.dx1, node.dy1)
                val end = current + Offset(node.dx2, node.dy2)
                points.addAll(sampleQuadraticBezier(current, control, end, samplesPerSegment))
                current = end
            }
            is PathNode.Close -> {
                points.addAll(sampleLine(current, start, samplesPerSegment))
                current = start
            }
            // Arc commands (ArcTo, RelativeArcTo) are more complex.
            else -> {}
        }
    }
    return points
}

// Helper: Linear interpolation
private fun sampleLine(
    start: Offset,
    end: Offset,
    samples: Int
): List<Offset> {
    if (samples < 2) return listOf(end)
    return (1..samples).map { i ->
        val t = i / samples.toFloat()
        start + (end - start) * t
    }
}

// Helper: Cubic Bézier sampling
private fun sampleCubicBezier(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    samples: Int
): List<Offset> {
    return (1..samples).map { i ->
        val t = i / samples.toFloat()
        val u = 1 - t
        p0 * (u * u * u) + p1 * (3 * u * u * t) + p2 * (3 * u * t * t) + p3 * (t * t * t)
    }
}

// Helper: Quadratic Bézier sampling
private fun sampleQuadraticBezier(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    samples: Int
): List<Offset> {
    return (1..samples).map { i ->
        val t = i / samples.toFloat()
        val u = 1 - t
        p0 * (u * u) + p1 * (2 * u * t) + p2 * (t * t)
    }
}