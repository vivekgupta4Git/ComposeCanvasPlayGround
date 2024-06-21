package com.example.mycomposeplayground

import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeplayground.ui.theme.MyComposePlayGroundTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalBox
import com.patrykandpatrick.vico.core.common.HorizontalPosition
import com.patrykandpatrick.vico.core.common.VerticalPosition
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.half
import com.patrykandpatrick.vico.core.common.orZero
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposePlayGroundTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    // color = MaterialTheme.colorScheme.background
                ) {
                    // Greeting("Android")
                    // DrawingDifferentLines()
                    DrawVicoGraph()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val newDp = remember {
        mutableStateOf(0.dp)
    }

    val density = LocalDensity.current

    Text(
        text = "Hello $name!",
        modifier = modifier
            .onPlaced {
                val xV = with(density) { it.positionInParent().x.toDp() }
                println("\uD83C\uDF4F position in dp = $xV ")
                println(
                    "🍏 $name onPlaced() " +
                            "Size = ${it.size} \n" +
                            "positionInParent: ${it.positionInParent().x} ${it.positionInParent().y}\n"
                )
                val myPosition = it.positionInParent().x
                val myPositionInDp = with(density) { myPosition.toDp() }
                newDp.value = 20.dp + myPositionInDp
                //  println( "MyPosition = ${myPosition.value} and in dp = ${with(density){ myPosition.value.toDp() }}")
                println("New Dp = ${newDp.value} before that myPositionInDp = $myPositionInDp")
            }
            .offset(x = newDp.value)
    )
}

@Composable
fun CanVasPlay() {
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp)
            .background(Color.White)
            .fillMaxSize()
            .height(60.dp)
    ) {

        /*  drawLine(
              start = Offset(x=10f,y=30f),
              end = Offset(x= size.width-10f,y=30f),
              color = Color.Blue
          )*/

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Blue),
                //    tileMode = TileMode.Repeated,
                end = Offset(size.width - 10f, 30f)
            ),
            start = Offset(10f, 30f),
            end = Offset(size.width - 10f, 30f),
            strokeWidth = 20f,

            )
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Green),
                center = Offset(size.width / 2, 60f)
            ),
            start = Offset(x = 100f, y = 60f),
            end = Offset(x = size.width - 100f, y = 60f),
            strokeWidth = 20f,
        )

        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
            start = Offset(10f, 90f),
            end = Offset(size.width - 10f, 90f),
            color = Color.Blue,
            strokeWidth = 5f
        )
        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = 25f),
            start = Offset(10f, 120f),
            end = Offset(size.width - 10f, 120f),
            color = Color.Blue,
            strokeWidth = 5f
        )
        val path = Path().apply {
            moveTo(10f, 100f)
            lineTo(500f, 100f)
            arcTo(
                Rect(450f, 100f, 550f, 200f),
                sweepAngleDegrees = -180f,
                forceMoveTo = false,
                startAngleDegrees = 90f
            )
            // arcTo(Rect(50f,100f,150f,200f), sweepAngleDegrees = 180f, startAngleDegrees = -90f, forceMoveTo = false)
            lineTo(500f, 200f)
            lineTo(10f, 200f)
            // lineTo(50f,500f)

        }
        drawPath(path, color = Color.Red, style = Fill)
        val newPath = Path().apply {
            arcTo(
                Rect(50f, 250f, 150f, 350f),
                sweepAngleDegrees = 359f,
                startAngleDegrees = 0f,
                forceMoveTo = false
            )
        }
        drawPath(newPath, color = Color.Blue, style = Fill)

        val iconSizeInDp = 80.dp
        val iconSizeInPx = with(density) { iconSizeInDp.toPx() }
        val horizontalMargin = with(density) { 10.dp.toPx() }
        val contentHeight = with(density) { 100.dp.toPx() }
        val curvePath = Path().apply {
            /*quadraticBezierTo(
                x1 =size.width,
                x2 = 0f,
                y1 = size.height,
                y2 = size.height
            )*/
            //  moveTo(0f,2000f)
            //     cubicTo(x3 = size.width, y3 = 0f,
            //       x1 = size.width/2, y1 = 0f,
            //      x2 = size.width/2, y2 = size.height)
            moveTo(horizontalMargin, size.height / 2)
            lineTo(size.width / 2 - iconSizeInPx / 2, size.height / 2)
            quadraticBezierTo(
                x2 = size.width / 2 - horizontalMargin + iconSizeInPx / 2,
                y2 = size.height / 2,
                x1 = size.width / 2 - horizontalMargin,
                y1 = size.height / 2 - iconSizeInPx / 2,
            )
            // moveTo(size.width/2 + 200f, size.height/2)
            lineTo(size.width - horizontalMargin, size.height / 2)
            lineTo(size.width - horizontalMargin, size.height / 2 + contentHeight)
            lineTo(horizontalMargin, size.height / 2 + contentHeight)
            close()


        }
        //   drawLine(Color.Green, start =  Offset(100f,0f), end = Offset(100f,300f), strokeWidth = 2f)
        drawPath(curvePath, color = Color.Black, style = Stroke(width = 2f))

        drawCircle(Color.Blue, radius = 100f, Offset(200f, 500f), style = Fill)
        drawOval(Color.Red, Offset(200f, 700f), style = Fill, size = Size(40f, 80f))
        drawLine(Color.Green, start = Offset(200f, 0f), end = Offset(200f, y = 500f))
        drawLine(Color.Green, start = Offset(0f, 500f), end = Offset(200f, 500f))
        drawCircle(
            color = Color.Magenta, radius = 100f, Offset(300f, 500f),
            style = Stroke(
                width = 5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = 100f),
                join = StrokeJoin.Bevel,
                cap = StrokeCap.Butt
            )
        )
        val path3 = Path().apply {
            moveTo(10f, 0f)
            lineTo(20f, 10f)
            lineTo(10f, 20f)
            lineTo(0f, 10f)
        }
        val pathEffect = PathEffect.stampedPathEffect(
            shape = newPath,
            advance = 140f,
            phase = 0f,
            style = StampedPathEffectStyle.Morph
        )
        drawCircle(
            color = Color.Red,
            radius = 100.dp.toPx(),
            center = Offset(300f, 800f),
            style = Stroke(
                width = 5.dp.toPx(),
                join = StrokeJoin.Miter,
                cap = StrokeCap.Butt,
                pathEffect = pathEffect
            )
        )

    }
}

@Composable
fun DrawingDifferentLines() {
    Canvas(modifier = smallCanvasModifier) {
        drawLine(
            start = Offset(10f, 10f),
            end = Offset(x = size.width - 10f, y = 10f),
            color = Color.Blue,
        )
        drawLine(
            color = Color.Red,
            start = Offset(x = 10f, y = 25f),
            end = Offset(x = size.width - 10f, y = 25f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Blue, Color.Green)
            ),
            start = Offset(x = 10f, y = 40f),
            end = Offset(x = size.width - 10f, y = 40f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Blue, Color.Green),
                center = Offset((size.width - 20f) / 2, 60f),
                tileMode = TileMode.Clamp

            ),
            start = Offset(x = 10f, y = 60f),
            end = Offset(x = size.width - 10f, y = 60f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Blue, Color.Green),
                center = Offset((size.width - 20f) / 2, 80f),
                tileMode = TileMode.Repeated

            ),
            start = Offset(x = 10f, y = 80f),
            end = Offset(x = size.width - 10f, y = 80f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Blue, Color.Green),
                center = Offset((size.width - 20f) / 2, 100f),
                tileMode = TileMode.Mirror

            ),
            start = Offset(x = 10f, y = 100f),
            end = Offset(x = size.width - 10f, y = 100f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Blue, Color.Green),
                center = Offset((size.width - 20f) / 2, 120f),
                tileMode = TileMode.Decal

            ),
            start = Offset(x = 10f, y = 120f),
            end = Offset(x = size.width - 10f, y = 120f),
            strokeWidth = 10f
        )
        drawLine(
            brush = Brush.sweepGradient(
                colors = listOf(Color.Red, Color.Green, Color.Blue),
                center = Offset(size.width / 4, 140f)
            ),
            start = Offset(x = 10f, y = 140f),
            end = Offset(x = size.width - 10f, y = 140f),
            strokeWidth = 20f,
        )
        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
            start = Offset(10f, 160f),
            end = Offset(size.width - 10f, 160f),
            color = Color.Cyan,
            strokeWidth = 10f
        )
        drawLine(
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = 25f),
            start = Offset(10f, 180f),
            end = Offset(size.width - 10f, 180f),
            color = Color.Cyan,
            strokeWidth = 10f
        )

        drawLine(
            pathEffect = PathEffect.stampedPathEffect(
                shape = diamondPath,
                advance = 15f,//Spacing between each stamped shape
                phase = 0f,
                style = StampedPathEffectStyle.Rotate
            ),
            start = Offset(10f, 200f),
            end = Offset(size.width - 10f, 200f),
            color = Color.Green,
            strokeWidth = 10f
        )
        drawLine(
            pathEffect = PathEffect.stampedPathEffect(
                shape = diamondPath,
                advance = 15f,//Spacing between each stamped shape
                phase = 0f, //offset for first stampede.
                style = StampedPathEffectStyle.Morph
            ),
            start = Offset(10f, 220f),
            end = Offset(size.width - 10f, 220f),
            color = Color.Green,
            strokeWidth = 10f
        )
        drawLine(
            pathEffect = PathEffect.stampedPathEffect(
                shape = diamondPath,
                advance = 25f,//Spacing between each stamped shape
                phase = 10f, //offset for first stampede.
                style = StampedPathEffectStyle.Morph
            ),
            start = Offset(10f, 240f),
            end = Offset(size.width - 10f, 240f),
            color = Color.Red,
            strokeWidth = 10f
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyComposePlayGroundTheme {
        //CanVasPlay()
        DrawingDifferentLines()
    }
}

val smallCanvasModifier = Modifier
    .padding(8.dp)
    .background(Color.White)
    .height(100.dp)
    .fillMaxWidth()
    .border(border = BorderStroke(width = 2.dp, color = Color.Black))

val bigCanvasModifier = Modifier
    .padding(8.dp)
    .background(Color.White)
    .height(150.dp)
    .fillMaxWidth()
    .border(border = BorderStroke(width = 2.dp, color = Color.Black))

val diamondPath = Path().apply {
    moveTo(10f, 0f)
    lineTo(5f, 5f)
    lineTo(0f, 0f)
    lineTo(5f, -5f)
    close()
}
private val x = (1..50).toList()

@Composable
fun DrawVicoGraph() {
    val extraKey = ExtraStore.Key<ClosedFloatingPointRange<Float>>()
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            modelProducer.tryRunTransaction {
                val y = x.map { Random.nextFloat() * 25 }
                updateExtras { extraStore ->
                    extraStore[extraKey] = y.average().toFloat()..y.max()
                }
                lineSeries { series(x, y) }
            }
        }
    }
    val ctx = LocalContext.current
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                listOf(rememberLineSpec(shader = DynamicShader.color(Color(0xffa485e0))))
            ),
            startAxis = rememberDrawableAxisVerticalStart(
                customDrawable = ctx.getDrawable(R.drawable.girafee),
                howFarBelowTheChart = 55.dp,
                howFarAboveTheChart = 55.dp,
                offsetX = 30.dp,
                startAxis = rememberStartAxis(
                    axis = LineComponent(
                        color = android.graphics.Color.RED
                    ),
                    label = rememberAxisLabelComponent(
                        color = Color.Red.copy(0.8f),
                        textSize = 8.sp,
                    ),
                    title = null,
                    titleComponent = rememberTextComponent(
                        color = Color.Red,
                        background = rememberShapeComponent(
                            shape = Shape.Rectangle
                        )
                    ),
//                tick = rememberLineComponent(
//                    color = Color.Red.copy(0.5f),
//                    shape = Shape.dashed(shape = Shape.Rectangle, gapLength = 10.dp, dashLength = 4.dp)
//                ),
//                tickLength = 500.dp,
                    tick = rememberLineComponent(
                        color = Color.Red
                    ),

                    guideline = null,
                    valueFormatter = { y, _, _ ->
                        DecimalFormat("00.##;−00.##").format(y)
                    }
                )
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                axis = rememberAxisLineComponent(
                    color = Color.Red
                ),
                tick = rememberLineComponent(
                    color = Color.Red.copy(0.5f)
                ),
                label = rememberTextComponent(
                    color = Color.Red
                )
            ),
            decorations = listOf(
                rememberCustomHorizontalBox(
                    y = {
                        it[extraKey]
                    }, box = rememberShapeComponent(
                        color = Color.Green.copy(alpha = 0.2f),
                        shape = Shape.Rectangle,
                    )
                )
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier.padding(vertical = 40.dp, horizontal = 0.dp),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

class CustomVerticalAxis(
    override val position: AxisPosition.Vertical.Start,
    var customDrawable: Drawable? = null,
    var howFarAboveTheChart: Int = 0,
    var howFarBelowTheChart: Int = 0,
    var offsetX: Int = 0,
) : VerticalAxis<AxisPosition.Vertical.Start>(position) {
    override fun drawBehindChart(context: CartesianDrawContext) {
        super.drawBehindChart(context)
        with(context) {

            customDrawable?.let { drawable ->
                 val drawableTop = bounds.top.toInt() - howFarAboveTheChart
                val drawableBottom = bounds.bottom.toInt() + howFarBelowTheChart
                drawable.setBounds(
                    bounds.left.toInt(),
                    drawableTop,
                    bounds.right.toInt() - axisThickness.toInt() + offsetX,
                    drawableBottom
                )
                drawable.draw(context.canvas)
            }
            val label = label
            val labelValues = itemPlacer.getLabelValues(
                context = context,
                bounds.height(),
                getMaxLabelHeight(),
                AxisPosition.Vertical.Start
            )
            val leftTick = bounds.right - bounds.left - axisThickness / 2
            val rightTick = leftTick + tickLength
            val yRange = chartValues.getYRange(AxisPosition.Vertical.Start)
            var tickCenterY: Float
            val labelX = bounds.right - axisThickness

            labelValues.forEach { labelValue ->
                tickCenterY =
                    bounds.bottom - bounds.height() * (labelValue - yRange.minY) / yRange.length +
                            getLineCanvasYCorrection(tickThickness, labelValue)
                tick?.drawHorizontal(
                    context = context,
                    left = leftTick,
                    right = rightTick,
                    centerY = tickCenterY
                )
                label ?: return@forEach
                drawLabel(
                    context = this,
                    label = label,
                    labelText = valueFormatter.format(labelValue, chartValues, position),
                    labelX = labelX,
                    tickCenterY = tickCenterY,
                )
            }
        }
    }

    override fun getDesiredWidth(context: CartesianMeasureContext, height: Float): Float =
        with(context) {
            val drawableWidth = customDrawable?.let { drawable ->
                (drawable.intrinsicWidth/2).toFloat()
            }.orZero
            val titleComponentWidth =
                title
                    ?.let { title ->
                        titleComponent?.getWidth(
                            context = this,
                            text = title,
                            rotationDegrees = 90f,
                            height = bounds.height().toInt(),
                        )
                    }
                    .orZero
            val labelSpace =
                when (horizontalLabelPosition) {
                    HorizontalLabelPosition.Outside -> {
                        val maxLabelWidth = getMaxLabelWidth(height).ceil
                        extraStore[maxLabelWidthKey] = maxLabelWidth
                        maxLabelWidth + tickLength
                    }
                    HorizontalLabelPosition.Inside -> 0f
                }

            when (val constraint = sizeConstraint) {
                is SizeConstraint.Auto -> {
                    if(drawableWidth == 0f){
                        (titleComponentWidth + axisThickness + labelSpace)
                            .coerceIn(
                                minimumValue = constraint.minSizeDp.pixels,
                                maximumValue = constraint.maxSizeDp.pixels,
                            )
                    }
                else
                    (axisThickness + drawableWidth).coerceIn(
                        minimumValue = constraint.minSizeDp.pixels,
                        maximumValue = constraint.maxSizeDp.pixels,
                    )
                }

                is SizeConstraint.Exact -> constraint.sizeDp.pixels
                is SizeConstraint.Fraction -> canvasBounds.width() * constraint.fraction
                is SizeConstraint.TextWidth ->
                    label
                        ?.getWidth(
                            context = this,
                            text = constraint.text,
                            rotationDegrees = labelRotationDegrees,
                        )
                        .orZero + axisThickness.half + drawableWidth
            }
        }

    override fun drawAboveChart(context: CartesianDrawContext) {
        // super.drawAboveChart(context)
        with(context){

            title?.let { title ->
                titleComponent?.drawText(
                    context = this,
                    text = title,
                    textX = bounds.left,
                    textY = bounds.centerY(),
                    horizontalPosition =HorizontalPosition.End,
                    verticalPosition = VerticalPosition.Center,
                    rotationDegrees = 90f * if (position.isStart) -1f else 1f,
                    maxTextHeight = bounds.height().toInt(),
                )
            }
        }
    }

    class Builder(
        builder: BaseAxis.Builder<AxisPosition.Vertical.Start>? = null
    ) {
        private val baseBuilder: VerticalAxis.Builder<AxisPosition.Vertical.Start> =
            VerticalAxis.Builder(builder)

        private var customDrawable: Drawable? = null
        fun build(position: AxisPosition.Vertical.Start): CustomVerticalAxis {
            return CustomVerticalAxis(position, customDrawable).apply {
                // Copy properties from baseBuilder to this instance
                this.label = baseBuilder.label
                this.axisLine = baseBuilder.axis
                this.tick = baseBuilder.tick
                this.guideline = baseBuilder.guideline
                this.valueFormatter = baseBuilder.valueFormatter
                this.tickLengthDp = baseBuilder.tickLengthDp
                this.sizeConstraint = baseBuilder.sizeConstraint
                this.horizontalLabelPosition = baseBuilder.horizontalLabelPosition
                this.verticalLabelPosition = baseBuilder.verticalLabelPosition
                this.itemPlacer = baseBuilder.itemPlacer
                this.labelRotationDegrees = baseBuilder.labelRotationDegrees
                this.titleComponent = baseBuilder.titleComponent
                this.title = baseBuilder.title
            }
        }
    }


    companion object {
        fun build(position: AxisPosition.Vertical.Start): CustomVerticalAxis {
            return Builder().build(position)
        }
    }
}


@Composable
fun rememberDrawableAxisVerticalStart(
    startAxis: VerticalAxis<AxisPosition.Vertical.Start> = rememberStartAxis(),
    customDrawable: Drawable? = null,
    howFarAboveTheChart: Dp = 0.dp,
    howFarBelowTheChart: Dp =0.dp,
    offsetX: Dp = 0.dp,
): CustomVerticalAxis {
    val (top,below,offset) = with(LocalDensity.current){
            Triple(howFarAboveTheChart.roundToPx(),howFarBelowTheChart.roundToPx(),offsetX.roundToPx())
    }
    return remember {

        CustomVerticalAxis.build(AxisPosition.Vertical.Start).apply {
            this.howFarAboveTheChart = top
            this.howFarBelowTheChart = below
            this.offsetX = offset
            this.customDrawable = customDrawable
            this.label = startAxis.label
            axisLine = startAxis.axisLine
            this.tick = startAxis.tick
            this.guideline = startAxis.guideline
            this.valueFormatter = startAxis.valueFormatter
            tickLengthDp = startAxis.tickLengthDp
            this.sizeConstraint = startAxis.sizeConstraint
            this.horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside
            this.verticalLabelPosition = startAxis.verticalLabelPosition
            this.itemPlacer = startAxis.itemPlacer
            this.labelRotationDegrees = startAxis.labelRotationDegrees
            this.titleComponent = startAxis.titleComponent
            this.title = startAxis.title
        }
    }
}

class CustomHorizontalBox(
    private val y: (ExtraStore) -> ClosedFloatingPointRange<Float>,
    private val box: ShapeComponent,
    private val labelComponent: TextComponent? = null,
    private val label: (ExtraStore) -> CharSequence = { getLabel(y(it)) },
    private val horizontalLabelPosition: HorizontalPosition = HorizontalPosition.Start,
    private val verticalLabelPosition: VerticalPosition = VerticalPosition.Top,
    private val labelRotationDegrees: Float = 0f,
    private val verticalAxisPosition: AxisPosition.Vertical? = null,
) : Decoration {
    override fun onDrawBehindChart(context: CartesianDrawContext, bounds: RectF) {
        with(context) {
            val yRange = chartValues.getYRange(verticalAxisPosition)
            val extraStore = chartValues.model.extraStore
            val y = y(extraStore)
            val label = label(extraStore)
            val topY =
                bounds.bottom - (y.endInclusive - yRange.minY) / yRange.length * bounds.height()
            val bottomY = bounds.bottom - (y.start - yRange.minY) / yRange.length * bounds.height()
            val labelY =
                when (verticalLabelPosition) {
                    VerticalPosition.Top -> topY
                    VerticalPosition.Center -> (topY + bottomY).half
                    VerticalPosition.Bottom -> bottomY
                }
            box.draw(context, bounds.left, topY, bounds.right, bottomY)
            labelComponent?.drawText(
                context = context,
                text = label,
                textX =
                when (horizontalLabelPosition) {
                    HorizontalPosition.Start -> bounds.getStart(isLtr)
                    HorizontalPosition.Center -> bounds.centerX()
                    HorizontalPosition.End -> bounds.getEnd(isLtr)
                },
                textY = labelY,
                horizontalPosition = -horizontalLabelPosition,
                verticalPosition =
                verticalLabelPosition.inBounds(
                    bounds = bounds,
                    componentHeight =
                    labelComponent.getHeight(
                        context = context,
                        text = label,
                        rotationDegrees = labelRotationDegrees,
                    ),
                    y = labelY,
                ),
                maxTextWidth = bounds.width().toInt(),
                rotationDegrees = labelRotationDegrees,
            )
        }

    }

    private fun RectF.getStart(isLtr: Boolean): Float = if (isLtr) left else right

    private fun RectF.getEnd(isLtr: Boolean): Float = if (isLtr) right else left

    /** @suppress */
    companion object {
        val decimalFormat = DecimalFormat("#.##;−#.##")

        fun getLabel(y: ClosedFloatingPointRange<Float>): String =
            "${decimalFormat.format(y.start)}–${decimalFormat.format(y.endInclusive)}"
    }

    operator fun HorizontalPosition.unaryMinus() =
        when (this) {
            HorizontalPosition.Start -> HorizontalPosition.End
            HorizontalPosition.Center -> HorizontalPosition.Center
            HorizontalPosition.End -> HorizontalPosition.Start
        }

    private fun VerticalPosition.inBounds(
        bounds: RectF,
        distanceFromPoint: Float = 0f,
        componentHeight: Float,
        y: Float,
    ): VerticalPosition {
        val topFits = y - distanceFromPoint - componentHeight >= bounds.top
        val centerFits =
            y - componentHeight.half >= bounds.top && y + componentHeight.half <= bounds.bottom
        val bottomFits = y + distanceFromPoint + componentHeight <= bounds.bottom

        return when (this) {
            VerticalPosition.Top -> if (topFits) this else VerticalPosition.Bottom
            VerticalPosition.Bottom -> if (bottomFits) this else VerticalPosition.Top
            VerticalPosition.Center ->
                when {
                    centerFits -> this
                    topFits -> VerticalPosition.Top
                    else -> VerticalPosition.Bottom
                }
        }
    }
}

@Composable
fun rememberCustomHorizontalBox(
    y: (ExtraStore) -> ClosedFloatingPointRange<Float>,
    box: ShapeComponent,
    labelComponent: TextComponent? = null,
    label: (ExtraStore) -> CharSequence = { HorizontalBox.getLabel(y(it)) },
    horizontalLabelPosition: HorizontalPosition = HorizontalPosition.Start,
    verticalLabelPosition: VerticalPosition = VerticalPosition.Top,
    labelRotationDegrees: Float = 0f,
    verticalAxisPosition: AxisPosition.Vertical? = null,
): CustomHorizontalBox =
    remember(
        y,
        box,
        labelComponent,
        label,
        horizontalLabelPosition,
        verticalLabelPosition,
        labelRotationDegrees,
        verticalAxisPosition,
    ) {
        CustomHorizontalBox(
            y,
            box,
            labelComponent,
            label,
            horizontalLabelPosition,
            verticalLabelPosition,
            labelRotationDegrees,
            verticalAxisPosition,
        )
    }


inline val Float.ceil: Float
    get() = ceil(this)