package com.example.applicationchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applicationchart.ui.theme.ApplicationChartTheme
import com.example.applicationchart.ui.theme.Pink40
import com.example.applicationchart.ui.theme.Purple40
import com.example.applicationchart.ui.theme.Purple80
import com.example.applicationchart.ui.theme.PurpleGrey40
import com.example.applicationchart.ui.theme.white
import java.lang.Math.PI
import java.lang.Math.atan2
import kotlin.math.atan2
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationChartTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(white)
                        .padding(5.dp)
                    ,
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        "Chart App",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple40,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        PieChart(
                            modifier = Modifier
                                .size(500.dp),
                            input = listOf(
                                PieChartInput(
                                    color = Purple80,
                                    value = 55,
                                    description = "Tarik Tunai"
                                ),
                                PieChartInput(
                                    color = Purple40,
                                    value = 31,
                                    description = "QRIS Payment"
                                ),
                                PieChartInput(
                                    color = PurpleGrey40,
                                    value = 7.7.roundToInt(),
                                    description = "Topup Gopay"
                                ),
                                PieChartInput(
                                    color = Pink40,
                                    value = 6.3.roundToInt(),
                                    description = "Lainnya"
                                ),

                            ),
                            centerText = "See Value"
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius:Float = 500f,
    innerRadius:Float = 250f,
    transparentWidth:Float = 70f,
    input:List<PieChartInput>,
    centerText:String = ""
){
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var inputList by remember {
        mutableStateOf(input)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true){
                    detectTapGestures(
                        onTap = { offset->
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)
                            val centerClicked = if(tapAngleInDegrees<90) {
                                offset.x<circleCenter.x+innerRadius && offset.y<circleCenter.y+innerRadius
                            }else if(tapAngleInDegrees<180){
                                offset.x>circleCenter.x-innerRadius && offset.y<circleCenter.y+innerRadius
                            }else if(tapAngleInDegrees<270){
                                offset.x>circleCenter.x-innerRadius && offset.y>circleCenter.y-innerRadius
                            }else{
                                offset.x<circleCenter.x+innerRadius && offset.y>circleCenter.y-innerRadius
                            }

                            if(centerClicked){
                                inputList = inputList.map {
                                    it.copy(isTapped = !isCenterTapped)
                                }
                                isCenterTapped = !isCenterTapped
                            }else{
                                val anglePerValue = 360f/input.sumOf {
                                    it.value
                                }
                                var currAngle = 0f
                                inputList.forEach { pieChartInput ->

                                    currAngle += pieChartInput.value * anglePerValue
                                    if(tapAngleInDegrees<currAngle){
                                        val description = pieChartInput.description
                                        inputList = inputList.map {
                                            if(description == it.description){
                                                it.copy(isTapped = !it.isTapped)
                                            }else{
                                                it.copy(isTapped = false)
                                            }
                                        }
                                        return@detectTapGestures
                                    }
                                }
                            }
                        }
                    )
                }
        ){
            val width = size.width
            val height = size.height
            circleCenter = Offset(x= width/2f,y= height/2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            inputList.forEach { pieChartInput ->
                val scale = if(pieChartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale){
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height - radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle-angleToDraw/2f-90f
                var factor = 1f
                if(rotateAngle>90f){
                    rotateAngle = (rotateAngle+180).mod(360f)
                    factor = -0.92f
                }

                val percentage = (pieChartInput.value/totalValue.toFloat()*100).toInt()

                drawContext.canvas.nativeCanvas.apply {
                    if(percentage>3){
                        rotate(rotateAngle){
                            drawText(
                                "$percentage %",
                                circleCenter.x,
                                circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                                Paint().apply {
                                    textSize = 13.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = white.toArgb()
                                }
                            )
                        }
                    }
                }
                if(pieChartInput.isTapped){
                    val tabRotation = currentStartAngle - angleToDraw - 90f
                    rotate(tabRotation){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f,radius*1.2f),
                            color = white,
                            cornerRadius = CornerRadius(15f,15f)
                        )
                    }
                    rotate(tabRotation+angleToDraw){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f,radius*1.2f),
                            color = white,
                            cornerRadius = CornerRadius(15f,15f)
                        )
                    }
                    rotate(rotateAngle){
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "${pieChartInput.description}: ${pieChartInput.value}",
                                circleCenter.x,
                                circleCenter.y + radius*1.3f*factor,
                                Paint().apply {
                                    textSize = 22.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = Purple40.toArgb()
                                    isFakeBoldText = true
                                }
                            )
                        }
                    }
                }
            }

            if(inputList.first().isTapped){
                rotate(-90f){
                    drawRoundRect(
                        topLeft = circleCenter,
                        size = Size(12f,radius*1.2f),
                        color = white,
                        cornerRadius = CornerRadius(15f,15f)
                    )
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = white.copy(alpha = 0.6f).toArgb()
                        setShadowLayer(10f,0f,0f, white.toArgb())
                    }
                )
            }

            drawCircle(
                color = white.copy(0.2f),
                radius = innerRadius+transparentWidth/2f
            )

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius/1.5f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )

    }
}


data class PieChartInput(
    val color: Color,
    val value:Int,
    val description:String,
    val isTapped:Boolean = false
)

