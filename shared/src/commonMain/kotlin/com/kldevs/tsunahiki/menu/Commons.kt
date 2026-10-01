package com.kldevs.tsunahiki.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.game.GamePhase
import com.kldevs.tsunahiki.game.GameState
import com.kldevs.tsunahiki.game.OnEventFn
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.navigation.NavFn
import org.jetbrains.compose.resources.painterResource
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.back
import tsunahiki.shared.generated.resources.ui_coins

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        TextDisplay("Loading...")
    }
}

@Composable
fun TextDisplay(text: String, color: Color? = null, fontSize: TextUnit = 18.sp, fontWeight: FontWeight = FontWeight.W800) {
    Text(
        text,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        color = color ?: MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

@Composable
fun LeftBox(content: @Composable() (BoxScope.() -> Unit)) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        content = content,
    )
}

@Composable
fun TextCoins(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(50.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Image(
            painter = painterResource(Res.drawable.ui_coins),
            contentDescription = "Coins",
            modifier = Modifier.size(18.dp),
        )
        TextDisplay(
            text,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun DialogBackground(watchFor: Boolean) {
    val bgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val transparent = Color.Transparent
    val bg by animateColorAsState(
        targetValue = if (watchFor) bgColor else transparent,
        animationSpec = tween(500),
        label = "Bg animation",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    )
}

data class DialogOption(
    val text: String,
    val onClick: () -> Unit,
    val isDestructive: Boolean,
)

@Composable
fun DialogMenu(title: String, isVisible: Boolean, options: List<DialogOption>, content: @Composable() ColumnScope.() -> Unit) {
    val anim = remember { Animatable(1.0f) }

    LaunchedEffect(isVisible) {
        anim.animateTo(
            targetValue = if(isVisible) 0.0f else 1.0f,
            animationSpec = tween(1000),
        )
    }

    DialogBackground(isVisible)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = size.height * anim.value
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Box(
                modifier = Modifier.padding(10.dp),
            ) {
                TextDisplay(title)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                content = content,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                options.forEach { (text, onClick, isDestructive) ->
                    Box(
                        modifier = Modifier.weight(1.0f).fillMaxWidth().padding(10.dp),
                    ) {
                        SpringButton(
                            onClick = onClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(25.dp),
                        ) {
                            TextDisplay(
                                text,
                                color = if(isDestructive) MaterialTheme.colorScheme.error else null,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.W500,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 4.dp,
) = this.drawWithCache {
    val stroke = strokeWidth.toPx()
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(dashLength.toPx(), gapLength.toPx()),
        0f,
    )
    val outline = shape.createOutline(size, layoutDirection, this)
    onDrawBehind {
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(width = stroke, pathEffect = pathEffect),
        )
    }
}

@Composable
fun BackgroundColumn(modifier: Modifier = Modifier, width: Float = 1.0f, borderColor: Color? = null, isBorderDashed: Boolean = false, content: @Composable() (ColumnScope.() -> Unit)) {
    val surfaceBright = MaterialTheme.colorScheme.surfaceBright
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surfaceDim = MaterialTheme.colorScheme.surfaceDim
    val onSurface = MaterialTheme.colorScheme.onSurface

    val border = borderColor ?: secondary

    Column(
        modifier = modifier
            .fillMaxWidth(width)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceBright)
            .then(
                 if(!isBorderDashed)
                     Modifier.border(5.dp, border, RoundedCornerShape(12.dp))
                else
                    Modifier.dashedBorder(border, RoundedCornerShape(12.dp), strokeWidth = 5.dp)
            )
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        content = content,
    )
}

@Composable
fun IconButton(painter: Painter, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val surface = MaterialTheme.colorScheme.surface

    SpringButton(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        modifier = modifier,
        containerColor = surface,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun Menu(title: String, onBack: NavFn, topContent: @Composable() () -> Unit = {}, content: @Composable() () -> Unit) {
    val surface = MaterialTheme.colorScheme.surface
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            IconButton(
                painter = painterResource(Res.drawable.back),
                contentDescription = "Back",
                onClick = { onBack() },
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                title,
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.W800,
            )

            Spacer(modifier = Modifier.weight(1.0f).fillMaxWidth())

            topContent()
        }

        content()
    }
}