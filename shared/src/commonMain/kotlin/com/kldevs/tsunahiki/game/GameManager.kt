package com.kldevs.tsunahiki.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kldevs.tsunahiki.GameGrade
import com.kldevs.tsunahiki.GameRules
import com.kldevs.tsunahiki.audio.AudioEngine
import com.kldevs.tsunahiki.game.character.ICharacterCatalog
import com.kldevs.tsunahiki.game.character.KanaCatalog
import com.kldevs.tsunahiki.game.utils.CanvasStroke
import com.kldevs.tsunahiki.game.utils.SpringButton
import com.kldevs.tsunahiki.game.utils.SpringText
import com.kldevs.tsunahiki.game.utils.normalizeOffset
import com.kldevs.tsunahiki.game.utils.similarity
import com.kldevs.tsunahiki.game.utils.unnormalizeOffset
import com.kldevs.tsunahiki.menu.DialogMenu
import com.kldevs.tsunahiki.menu.DialogOption
import com.kldevs.tsunahiki.menu.LoadingView
import com.kldevs.tsunahiki.menu.MatchRewardsMenu
import com.kldevs.tsunahiki.menu.PauseMenu
import com.kldevs.tsunahiki.menu.TextCoins
import com.kldevs.tsunahiki.menu.TextDisplay
import com.kldevs.tsunahiki.navigation.GameplayRoute
import com.kldevs.tsunahiki.navigation.NavFn
import com.kldevs.tsunahiki.navigation.NavToFn
import com.kldevs.tsunahiki.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.hint
import tsunahiki.shared.generated.resources.pause
import tsunahiki.shared.generated.resources.sound
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CircleButton(onClick: () -> Unit, content: @Composable() RowScope.() -> Unit) {
    SpringButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp),
        content = content,
    )
}

@Composable
fun MainTop(gameState: GameState, onEvent: OnEventFn) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(
            onClick = {
                onEvent(GameEvent.Pause(true))
            },
        ) {
            Image(painter = painterResource(Res.drawable.pause), contentDescription = "Pause")
        }
        Box(
            modifier = Modifier.width(125.dp),
            contentAlignment = Alignment.Center,
        ) {
            val guidedText = if(gameState.isGuided) "Guided - Kana" else "Unguided - Kana"

            Text(guidedText, fontFamily = MaterialTheme.typography.displayMedium.fontFamily, fontWeight = FontWeight.W600)
        }
        Box(
            modifier = Modifier
                .width(85.dp)
                .height(35.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.surfaceBright)
                .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.Center,
        ) {
            SpringText(
                gameState.score.toString(),
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontWeight = FontWeight.W600,
                watchFor = gameState.score,
            )
        }
    }
}

@Composable
fun MainPlayer(player: PlayerDisplay)
{
    Column(
        modifier = Modifier.width(60.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(GameAvatars.getDrawable(player.avatar)),
                contentDescription = "Avatar",
                contentScale = ContentScale.FillBounds,
            )
        }
        Text(
            player.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.W600,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
fun MainRope(gameState: GameState) {
    val color = MaterialTheme.colorScheme.onSurface
    val flagColor = MaterialTheme.colorScheme.primary

    val flagMoveAnim = remember { Animatable(gameState.flagScore) }
    val flagMoveTipAnim = remember { Animatable(gameState.flagScore) }

    LaunchedEffect(gameState.flagScore) {
        launch {
            flagMoveAnim.animateTo(
                targetValue = gameState.flagScore, animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            )
        }
        launch {
            flagMoveTipAnim.animateTo(
                targetValue = gameState.flagScore, animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            )
        }
    }

    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        Canvas(
            modifier = Modifier.width(125.dp).height(10.dp)
        ) {
            val flagMoveValue = flagMoveAnim.value / GameRules.MAX_FLAG_SCORE * size.width / 2.0f
            val flagMoveTipValue =
                flagMoveTipAnim.value / GameRules.MAX_FLAG_SCORE * size.width / 2.0f

            drawLine(
                color = color,
                start = Offset(0.0f, size.height / 10.0f),
                end = Offset(size.width, size.height / 10.0f),
                strokeWidth = 10.0f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20.0f, 20.0f), -flagMoveValue)
            )

            val path = Path().apply {
                moveTo(size.width / 2.0f - 25.0f + flagMoveValue, size.height / 10.0f - 5.0f)
                lineTo(size.width / 2.0f + 25.0f + flagMoveValue, size.height / 10.0f - 5.0f)
                lineTo(size.width / 2.0f + flagMoveTipValue, size.height + 10.0f)
                close()
            }
            drawPath(
                path = path,
                color = flagColor,
            )
        }
    }
}

@Composable
fun MainMiddle(gameState: GameState, onEvent: OnEventFn) {
    val displayCharacter = gameState.characterDesc!!.getDisplay()

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surfaceBright)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(15.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceBright),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MainPlayer(gameState.playerDisplay)
                MainRope(gameState)
                MainPlayer(gameState.enemyDisplay)
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.width(280.dp).fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleButton(
                    onClick = {
                        onEvent(GameEvent.RequestHearSound)
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.sound),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .size(18.dp),
                    )
                }

                Box(
                    modifier = Modifier.size(128.dp).clip(RoundedCornerShape(15.dp))
                        .background(MaterialTheme.colorScheme.surfaceBright).border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant),
                            RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        displayCharacter,
                        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.W800,
                    )
                }

                CircleButton(
                    onClick = {
                        onEvent(GameEvent.RequestGuide(false))
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.hint),
                        contentDescription = "Hint",
                        modifier = Modifier
                            .size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun MainCanvas(gameState: GameState, modifier: Modifier, onEvent: OnEventFn) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val colorGuide = MaterialTheme.colorScheme.surfaceContainerLow
    val error = MaterialTheme.colorScheme.error
    val resolveColor = remember(gameState.recentGrade) {
        when(gameState.recentGrade) {
            GameGrade.Perfect -> Color(0xFF5EB053)
            GameGrade.Good -> Color(0xFFAEB053)
            GameGrade.Okay -> Color(0xFFB09353)
            GameGrade.Miss -> error
        }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val strokeSize = 50.0f
    val guideStrokes = gameState.guideStrokes
    val guideStrokesSize = guideStrokes.size
    var userStroke by remember { mutableStateOf<CanvasStroke?>(null) }

    val guideStrokeAnims = remember(guideStrokes) {
        List(guideStrokesSize) { Animatable(0.0f) }
    }

    LaunchedEffect(guideStrokes, gameState.requestGuide) {
        if(!gameState.requestGuide || guideStrokes.isEmpty())
            return@LaunchedEffect

        val duration = 1000
        val pauseDuration = 200
        guideStrokeAnims.forEach { it.snapTo(0.0f) }
        guideStrokeAnims.forEachIndexed { index, anim ->
            launch {
                val delayMs = duration * index + pauseDuration
                anim.animateTo(
                    1.0f,
                    animationSpec = tween(duration, delayMs, FastOutSlowInEasing)
                )
            }
        }
        delay(((duration + pauseDuration) * guideStrokeAnims.size).milliseconds)
        onEvent(GameEvent.GuideFinished)
    }

    Canvas(
        modifier = modifier
            .onSizeChanged {
                canvasSize = Size(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val s = Offset(size.width.toFloat(), size.height.toFloat())

                    val drag = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                        val d = normalizeOffset(s, down.position)
                        val c = normalizeOffset(s, change.position)
                        userStroke = CanvasStroke(
                            points = mutableStateListOf(d, c),
                            color = color,
                        )
                        // userStroke.let { onEvent(GameEvent.StrokeCommitted(it)) }
                        change.consume()
                    }

                    if(drag == null) {
                        onEvent(GameEvent.StrokeCommitted(
                            CanvasStroke(
                                points = mutableStateListOf(down.position),
                                color = color,
                            )
                        ))
                        return@awaitEachGesture
                    }

                    while(true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if(!change.pressed) break
                        change.consume()

                        val last = userStroke?.points?.lastOrNull()
                        val norm = normalizeOffset(s, change.position)
                        if((last == null) || ((norm - last).getDistance() > 0.025f))
                            userStroke?.points?.add(norm)
                    }

                    onEvent(GameEvent.StrokeCommitted(userStroke!!))
                    userStroke = null
                }
            },
    ) {
        drawLine(
            color = colorGuide,
            start = Offset(size.width / 2.0f, 0.0f),
            end = Offset(size.width / 2.0f, size.height),
            strokeWidth = 5.0f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(25.0f, 25.0f), 0.0f),
        )
        drawLine(
            color = colorGuide,
            start = Offset(0.0f, size.height / 2.0f),
            end = Offset(size.width, size.height / 2.0f),
            strokeWidth = 5.0f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(25.0f, 25.0f), 0.0f),
        )

        val viewport = Offset(size.width, size.height)
        val center = Offset(size.width / 2.0f, size.height / 2.0f)
        val guideScale = size.minDimension * 0.5f

        guideStrokes.forEachIndexed { index, stroke ->
            val anim = guideStrokeAnims[index]
            val points = stroke.points.map { (x, y) ->
                Offset(center.x + x * guideScale, center.y + y * guideScale)
            }

            if(points.size == 1) {
                drawCircle(colorGuide, strokeSize / 2.0f, points[0])
                return@forEachIndexed
            }

            val path = Path().apply {
                if(points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for(i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

            }
            val measure = PathMeasure().apply { setPath(path, false) }
            val length = measure.length

            val pathToDraw = if(anim.value >= 1.0f) {
                path
            } else {
                Path().also { dst ->
                    measure.getSegment(0.0f, length * anim.value, dst, true)
                }
            }

            drawPath(pathToDraw, color = colorGuide, style = Stroke(width = strokeSize, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        fun drawStroke(stroke: CanvasStroke) {
            if(stroke.points.size == 1) {
                drawCircle(color, strokeSize / 2.0f, stroke.points[0])
                return
            }

            val path = Path().apply {
                val points = stroke.points
                if(points.isNotEmpty()) {
                    val initial = unnormalizeOffset(viewport,points[0])
                    moveTo(initial.x, initial.y)
                    for(i in 1 until points.size) {
                        val point = unnormalizeOffset(viewport, points[i])
                        lineTo(point.x, point.y)
                    }
                }
            }

            val col = if(gameState.phase == GamePhase.Resolving)
                resolveColor
            else color

            drawPath(path, color = col, style = Stroke(width = strokeSize, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        gameState.userStrokes.forEach { stroke ->
            drawStroke(stroke)
        }
        if(userStroke != null)
            drawStroke(userStroke!!)
    }
}

@Composable
fun MainBottom(gameState: GameState, onEvent: OnEventFn) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val error = MaterialTheme.colorScheme.error
    val primary = MaterialTheme.colorScheme.primary
    val strokeTextColor = remember(gameState.totalStrokes) {
        if(gameState.totalStrokes > gameState.maxStrokes)
            error
        else if(gameState.totalStrokes == gameState.maxStrokes)
            primary
        else
            onSurface
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(15.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Canvas",
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            SpringText(
                "${gameState.totalStrokes}/${gameState.maxStrokes}",
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 13.sp,
                color = strokeTextColor,
                watchFor = gameState.totalStrokes,
            )
        }
        MainCanvas(
            onEvent = onEvent,
            gameState = gameState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surfaceBright)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant), RoundedCornerShape(15.dp)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    onEvent(GameEvent.Clear)
                },
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(0.95f),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text("Clear")
            }
            Button(
                onClick = {
                    onEvent(GameEvent.Undo)
                },
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(0.95f),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text("Undo")
            }
        }
    }
}

@Composable
private fun StartingView(gameState: GameState) {
    DialogMenu(
        "Starting...",
        isVisible = gameState.phase == GamePhase.Starting,
        options = listOf(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.Center,
        ) {
            TextDisplay(
                "Starting in ${gameState.startingCount}",
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
            )
        }
    }
}

@Composable
private fun NotEnoughCoinsView(gameState: GameState, onDismiss: () -> Unit) {
    DialogMenu(
        title = "Not Enough Coins!",
        isVisible = gameState.phase == GamePhase.GuideNotEnoughMoney,
        options = listOf(
            DialogOption("Okay", onDismiss, false),
        ),
    ) {}
}

@Composable
private fun ConfirmedView(gameState: GameState, onDismiss: () -> Unit) {
    DialogMenu(
        title = "Successfully Purchased",
        isVisible = gameState.phase == GamePhase.GuideConfirm,
        options = listOf(
            DialogOption("Okay", onDismiss, false),
        ),
    ) {}
}

@Composable
private fun GuidedView(gameState: GameState, onEvent: OnEventFn) {
    val guidedCost = 100

    DialogMenu(
        "This is Unguided!",
        isVisible = gameState.phase == GamePhase.GuideDialog,
        options = listOf(
            DialogOption("No", {
                onEvent(GameEvent.DismissGuideDialog)
            }, true),
            DialogOption("Buy", {
                onEvent(GameEvent.GuideConfirm)
            }, false),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            TextDisplay(
                "Using a guide in unguided will cost you",
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
            )
            TextCoins(guidedCost.toString())
        }
    }

    ConfirmedView(gameState, {
        onEvent(GameEvent.RequestGuide(true))
    })
    NotEnoughCoinsView(gameState, {
        onEvent(GameEvent.DismissGuideDialog)
    })
}

@Composable
fun MainGame(isGuided: Boolean, onBack: NavFn, navTo: NavToFn) {
    val scope = rememberCoroutineScope()
    val audioEngine = koinInject<AudioEngine>()
    val settings = koinInject<GameSettings>()
    val playerRepo = koinInject<PlayerRepository>()
    val playerState = remember { PlayerState().apply { applySave(playerRepo.load()) } }
    val catalog = KanaCatalog.getOrNull()
    val gameLogic = remember {
        GameLogic(playerRepo, settings, playerState, catalog!!, scope, audioEngine)
    }
    val gameState by gameLogic.state.collectAsState()

    LaunchedEffect(gameLogic) {
        gameLogic.start(isGuided)
    }

    LaunchedEffect(gameState.phase) {
        if(gameState.phase != GamePhase.GameOver)
            return@LaunchedEffect

        gameLogic.onEvent(GameEvent.GameOverSound)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                MainTop(gameState, gameLogic::onEvent)
            }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                MainMiddle(gameState, gameLogic::onEvent)
            }
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                MainBottom(gameState, gameLogic::onEvent)
            }
        }
    }
    PauseMenu(gameState, gameLogic::onEvent)
    StartingView(gameState)
    GuidedView(gameState, gameLogic::onEvent)

    if(gameState.phase == GamePhase.GameOver) {
        val isGameOver = gameState.phase == GamePhase.GameOver
        val isWin = gameState.gameOverState == GameOverState.Win
        val rewards = calculateRewards(catalog!!.getLanguage(), isWin, gameState.score, playerState)

        MatchRewardsMenu(
            gameState,
            rewards,
            onRematch = {
                onBack()
                navTo(GameplayRoute(isGuided))
            },
            onSettings = {
                onBack()
            },
        )
    }
}

@Preview
@Composable
fun PreviewGame() {
    AppTheme {
    }
}