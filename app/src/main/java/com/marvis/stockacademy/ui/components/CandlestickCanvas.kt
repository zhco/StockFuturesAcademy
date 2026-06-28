package com.marvis.stockacademy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.marvis.stockacademy.DownGreen
import com.marvis.stockacademy.UpRed
import kotlin.math.max
import kotlin.math.min

data class CandlestickData(val open: Float, val high: Float, val low: Float, val close: Float, val label: String = "")
data class LineData(val values: List<Float>, val color: Color, val label: String = "")

@Composable
fun SingleCandlestick(data: CandlestickData, modifier: Modifier = Modifier, title: String = "", annotations: List<Pair<String, Float>> = emptyList()) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (title.isNotEmpty()) Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val w = size.width; val h = size.height
            val minP = data.low * 0.95f; val maxP = data.high * 1.05f; val range = maxP - minP
            fun py(p: Float) = h - ((p - minP) / range) * h * 0.8f - h * 0.1f
            val cx = w / 2; val bh = w * 0.08f; val isUp = data.close >= data.open
            val bt = py(max(data.open, data.close)); val bb = py(min(data.open, data.close))
            val hy = py(data.high); val ly = py(data.low)
            val lc = if (isUp) UpRed else DownGreen
            drawLine(lc, Offset(cx, hy), Offset(cx, ly), strokeWidth = 2f)
            if (isUp) drawRect(UpRed, Offset(cx - bh, bt), Size(bh * 2, max(bb - bt, 1f)))
            else { drawRect(Color.White, Offset(cx - bh, bt), Size(bh * 2, max(bb - bt, 1f))); drawRect(DownGreen, Offset(cx - bh, bt), Size(bh * 2, max(bb - bt, 1f)), style = Stroke(2f)) }
            annotations.forEach { (t, r) ->
                val y = py(minP + range * r)
                drawLine(Color.Gray, Offset(cx + bh + 20f, y), Offset(cx + bh + 60f, y))
                drawContext.canvas.nativeCanvas.drawText(t, cx + bh + 65f, y + 5f, android.graphics.Paint().apply { color = android.graphics.Color.DKGRAY; textSize = 28f; isAntiAlias = true })
            }
        }
    }
}

@Composable
fun CandlestickChart(dataList: List<CandlestickData>, modifier: Modifier = Modifier, title: String = "", maLines: List<LineData> = emptyList(), volumeData: List<Float> = emptyList()) {
    Column(modifier) {
        if (title.isNotEmpty()) Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 4.dp))
        Canvas(Modifier.fillMaxWidth().height(260.dp)) {
            if (dataList.isEmpty()) return@Canvas
            val w = size.width; val h = size.height
            val allP = dataList.flatMap { listOf(it.high, it.low) } + maLines.flatMap { it.values }
            val minP = allP.min() * 0.98f; val maxP = allP.max() * 1.02f; val range = maxP - minP
            fun py(p: Float) = h - 20f - ((p - minP) / range) * (h - 40f)
            val cw = (w - 16f) / dataList.size * 0.6f; val step = (w - 16f) / dataList.size
            dataList.forEachIndexed { i, d ->
                val cx = 8f + step * i + step / 2; val isUp = d.close >= d.open
                val col = if (isUp) UpRed else DownGreen
                drawLine(col, Offset(cx, py(d.high)), Offset(cx, py(d.low)), strokeWidth = 1f)
                val top = py(max(d.open, d.close)); val bot = py(min(d.open, d.close))
                if (isUp) drawRect(col, Offset(cx - cw, top), Size(cw * 2, max(bot - top, 1f)))
                else { drawRect(Color.White, Offset(cx - cw, top), Size(cw * 2, max(bot - top, 1f))); drawRect(col, Offset(cx - cw, top), Size(cw * 2, max(bot - top, 1f)), style = Stroke(1f)) }
            }
            maLines.forEach { line ->
                val path = Path()
                line.values.forEachIndexed { i, v ->
                    val x = 8f + step * i + step / 2; val y = py(v)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, line.color, style = Stroke(1.5f))
            }
        }
        if (volumeData.isNotEmpty()) {
            Canvas(Modifier.fillMaxWidth().height(60.dp)) {
                val w = size.width; val h = size.height
                val mv = volumeData.max() * 1.1f; val step = (w - 16f) / dataList.size; val bw = step * 0.6f
                volumeData.forEachIndexed { i, v ->
                    val cx = 8f + step * i + step / 2; val bh2 = (v / mv) * (h - 4f)
                    val isUp = dataList.getOrNull(i)?.let { it.close >= it.open } ?: true
                    drawRect(if (isUp) UpRed.copy(alpha = 0.5f) else DownGreen.copy(alpha = 0.5f), Offset(cx - bw, h - bh2), Size(bw * 2, bh2))
                }
            }
        }
    }
}

@Composable
fun PatternDiagram(patternType: String, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(220.dp)) {
        val w = size.width; val h = size.height
        when (patternType) {
            "hammer" -> { drawLine(DownGreen, Offset(w*.5f, h*.15f), Offset(w*.5f, h*.85f), strokeWidth=2f); drawRect(Color.White, Offset(w*.5f-12f, h*.45f), Size(24f, h*.1f)); drawRect(DownGreen, Offset(w*.5f-12f, h*.45f), Size(24f, h*.1f), style=Stroke(2f)) }
            "doji" -> { drawLine(Color.Gray, Offset(w*.5f, h*.2f), Offset(w*.5f, h*.8f), strokeWidth=2f); drawLine(Color.Gray, Offset(w*.5f-15f, h*.5f), Offset(w*.5f+15f, h*.5f), strokeWidth=2f) }
            "engulfing" -> {
                drawLine(DownGreen, Offset(w*.35f, h*.2f), Offset(w*.35f, h*.5f), strokeWidth=2f); drawRect(Color.White, Offset(w*.35f-8f, h*.42f), Size(16f, h*.08f)); drawRect(DownGreen, Offset(w*.35f-8f, h*.42f), Size(16f, h*.08f), style=Stroke(2f))
                drawLine(UpRed, Offset(w*.65f, h*.1f), Offset(w*.65f, h*.65f), strokeWidth=2f); drawRect(UpRed, Offset(w*.65f-18f, h*.35f), Size(36f, h*.3f))
            }
            "head_shoulders" -> {
                val path = Path(); path.moveTo(w*.05f, h*.6f); path.lineTo(w*.15f, h*.6f); path.cubicTo(w*.2f, h*.3f, w*.25f, h*.3f, w*.3f, h*.6f); path.cubicTo(w*.35f, h*.1f, w*.45f, h*.1f, w*.5f, h*.6f); path.cubicTo(w*.55f, h*.35f, w*.6f, h*.35f, w*.65f, h*.6f); path.lineTo(w*.95f, h*.6f)
                drawPath(path, Color.Blue, style=Stroke(3f)); drawLine(Color.Gray.copy(alpha=.5f), Offset(w*.3f, h*.6f), Offset(w*.65f, h*.6f))
                lp("左肩",w*.22f,h*.28f); lp("头部",w*.38f,h*.08f); lp("右肩",w*.58f,h*.32f); lp("颈线",w*.7f,h*.58f)
            }
            "double_top" -> {
                val path = Path(); path.moveTo(w*.05f, h*.75f); path.lineTo(w*.15f, h*.45f); path.cubicTo(w*.25f, h*.45f, w*.3f, h*.5f, w*.35f, h*.5f); path.cubicTo(w*.4f, h*.25f, w*.5f, h*.25f, w*.55f, h*.5f); path.cubicTo(w*.6f, h*.5f, w*.65f, h*.65f, w*.95f, h*.65f)
                drawPath(path, UpRed, style=Stroke(3f)); drawLine(Color.Gray.copy(alpha=.5f), Offset(w*.3f, h*.5f), Offset(w*.6f, h*.5f)); lp("顶1",w*.13f,h*.42f); lp("顶2",w*.48f,h*.22f); lp("颈线",w*.65f,h*.48f)
            }
            "triangle" -> {
                val up = Path(); up.moveTo(w*.1f, h*.3f); (0..50).forEach { i -> val x = w*.1f+(w*.8f*i/50); up.lineTo(x, h*.3f+(h*.4f*i/50)-10f+(kotlin.math.sin(i*.5f)*15f).toFloat()) }
                val lo = Path(); lo.moveTo(w*.1f, h*.7f); (0..50).forEach { i -> val x = w*.1f+(w*.8f*i/50); lo.lineTo(x, h*.7f-(h*.4f*i/50)+10f+(kotlin.math.sin(i*.5f)*15f).toFloat()) }
                drawPath(up, DownGreen, style=Stroke(2f)); drawPath(lo, UpRed, style=Stroke(2f))
            }
            "three_soldiers" -> { listOf(.2f,.5f,.8f).forEachIndexed{i,r-> val cx=w*r; drawLine(UpRed,Offset(cx,h*(.1f+i*.1f)),Offset(cx,h*(.5f+i*.15f)),strokeWidth=2f); drawRect(UpRed,Offset(cx-15f,h*(.35f+i*.1f)),Size(30f,h*(.18f+i*.02f))) } }
            "morning_star" -> {
                drawLine(DownGreen,Offset(w*.2f,h*.15f),Offset(w*.2f,h*.55f),strokeWidth=2f); drawRect(Color.White,Offset(w*.2f-12f,h*.4f),Size(24f,h*.15f)); drawRect(DownGreen,Offset(w*.2f-12f,h*.4f),Size(24f,h*.15f),style=Stroke(2f))
                drawLine(Color.Gray,Offset(w*.5f,h*.5f),Offset(w*.5f,h*.6f),strokeWidth=2f); drawLine(Color.Gray,Offset(w*.5f-8f,h*.55f),Offset(w*.5f+8f,h*.55f),strokeWidth=2f)
                drawLine(UpRed,Offset(w*.8f,h*.1f),Offset(w*.8f,h*.55f),strokeWidth=2f); drawRect(UpRed,Offset(w*.8f-12f,h*.3f),Size(24f,h*.25f))
            }
        }
    }
}

private fun DrawScope.lp(t: String, x: Float, y: Float) { drawContext.canvas.nativeCanvas.drawText(t, x, y, android.graphics.Paint().apply { color = android.graphics.Color.DKGRAY; textSize = 32f; isAntiAlias = true }) }
