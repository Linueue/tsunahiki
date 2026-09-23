package com.kldevs.tsunahiki.game.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

fun similarity(strokes1: List<CanvasStroke>, strokes2: List<CanvasStroke>): Float {
    val n = min(strokes1.size, strokes2.size)
    val m = max(strokes1.size, strokes2.size)

    var total = 0.0f

    for(i in 0 until n) {
        val (distance, _) = dtwSeg(strokes1[i].points, strokes2[i].points)
        total += distance
    }

    return total / n
}

/**
 * Point-to-Segment Distance DTW (DTW_seg)
 * =======================================
 *
 * Reference:
 *   Moussa, E. M., Lelore, T., & Mouchère, H. (2023).
 *   "Point to segment distance DTW for online handwriting signals matching."
 *   ICPRAM 2023, pp. 850-855.
 *   https://hal.science/hal-03914726
 */

fun sqDist(p: Offset, q: Offset): Float {
    val dx = p.x - q.x
    val dy = p.y - q.y
    return dx * dx + dy * dy
}

fun pointToSegmentSqDist(x: Offset, a: Offset, b: Offset): Float {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val axx = x.x - a.x
    val axy = x.y - a.y
    val bxx = x.x - b.x
    val bxy = x.y - b.y

    val abDotAx = abx * axx + aby * axy
    val abDotBx = abx * bxx + aby * bxy

    if (abDotAx < 0f) return sqDist(x, a)          // projects before a
    if (abDotBx > 0f) return sqDist(x, b)          // projects after b

    // perpendicular distance (projection inside the segment)
    val abSq = abx * abx + aby * aby
    val t = abDotAx / abSq
    val projX = a.x + t * abx
    val projY = a.y + t * aby
    val dx = x.x - projX
    val dy = x.y - projY
    return dx * dx + dy * dy
}

fun pointToSegmentCostMatrix(X: List<Offset>, Y: List<Offset>): Array<FloatArray> {
    val n = X.size
    val m = Y.size
    val C = Array(n) { FloatArray(m) }

    if (m >= 2) {
        for (i in 0 until n) {
            val xi = X[i]
            for (j in 0 until m - 1) {
                C[i][j] = pointToSegmentSqDist(xi, Y[j], Y[j + 1])
            }
            // Last column: no outgoing segment → point-to-point fallback
            C[i][m - 1] = sqDist(xi, Y[m - 1])
        }
    } else if (m == 1) {
        for (i in 0 until n) {
            C[i][0] = sqDist(X[i], Y[0])
        }
    }

    return C
}

private fun dtwDp(C: Array<FloatArray>): Triple<Float, List<Pair<Int, Int>>, Array<FloatArray>> {
    val n = C.size
    val m = if (n > 0) C[0].size else 0

    val D = Array(n) { FloatArray(m) { Float.POSITIVE_INFINITY } }
    // ptr: 0 = diag, 1 = up, 2 = left, -1 = none
    val ptr = Array(n) { IntArray(m) { -1 } }

    if (n == 0 || m == 0) {
        return Triple(0f, emptyList(), D)
    }

    D[0][0] = C[0][0]

    for (i in 0 until n) {
        for (j in 0 until m) {
            if (i == 0 && j == 0) continue
            var best = Float.POSITIVE_INFINITY
            var bptr = -1
            if (i > 0 && D[i - 1][j] < best) {
                best = D[i - 1][j]; bptr = 1
            }
            if (j > 0 && D[i][j - 1] < best) {
                best = D[i][j - 1]; bptr = 2
            }
            if (i > 0 && j > 0 && D[i - 1][j - 1] < best) {
                best = D[i - 1][j - 1]; bptr = 0
            }
            D[i][j] = C[i][j] + best
            ptr[i][j] = bptr
        }
    }

    // Backtrack
    val path = ArrayList<Pair<Int, Int>>()
    var i = n - 1
    var j = m - 1
    while (true) {
        path.add(i to j)
        if (i == 0 && j == 0) break
        when (ptr[i][j]) {
            0 -> { i -= 1; j -= 1 }
            1 -> { i -= 1 }
            2 -> { j -= 1 }
            else -> break
        }
    }
    path.reverse()

    return Triple(D[n - 1][m - 1], path, D)
}

fun dtwSeg(
    X: List<Offset>,
    Y: List<Offset>,
    normalize: Boolean = false
): Pair<Float, List<Pair<Int, Int>>> {
    val C = pointToSegmentCostMatrix(X, Y)
    val (total, path, _) = dtwDp(C)
    val result = if (normalize && path.isNotEmpty()) total / path.size else total
    return result to path
}