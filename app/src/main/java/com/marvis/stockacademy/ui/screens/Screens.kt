package com.marvis.stockacademy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marvis.stockacademy.DownGreen
import com.marvis.stockacademy.UpRed
import com.marvis.stockacademy.data.*
import com.marvis.stockacademy.ui.components.PatternDiagram
import com.marvis.stockacademy.ui.components.SingleCandlestick
import com.marvis.stockacademy.ui.components.CandlestickData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCategoryClick: (Category) -> Unit, onItemClick: (KnowledgeItem) -> Unit, onSearchClick: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("股票期货知识大全", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary),
            actions = { IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "搜索", tint = MaterialTheme.colorScheme.onPrimary) } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { LearningPathCard() }
            item { Text("知识分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item { CategoryGrid(onCategoryClick) }
            item { Text("入门必读", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(KnowledgeBase.allItems.filter { it.level == Level.BEGINNER }.take(5)) { KnowledgeCard(it) { onItemClick(it) } }
        }
    }
}

@Composable
private fun LearningPathCard() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("第一步", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text("基础知识 → 术语 → K线 → 指标 → 策略 → 风控", style = MaterialTheme.typography.bodyMedium)
                Text("建议每天30分钟，30天入门", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CategoryGrid(onClick: (Category) -> Unit) {
    val icons = mapOf(Category.BASIC to Icons.Default.School, Category.TERMINOLOGY to Icons.Default.Book, Category.KLINE to Icons.Default.CandlestickChart, Category.INDICATORS to Icons.Default.ShowChart, Category.FUTURES to Icons.Default.Inventory2, Category.STRATEGY to Icons.Default.Psychology, Category.RISK to Icons.Default.Shield, Category.PSYCHOLOGY to Icons.Default.SelfImprovement)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Category.entries.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    Card(Modifier.weight(1f).clickable { onClick(cat) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icons[cat] ?: Icons.Default.Help, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(cat.label, style = MaterialTheme.typography.labelLarge)
                            Text("${KnowledgeBase.getByCategory(cat).size}篇", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KnowledgeCard(item: KnowledgeItem, onClick: () -> Unit) {
    val lc = when (item.level) { Level.BEGINNER -> MaterialTheme.colorScheme.primary; Level.INTERMEDIATE -> MaterialTheme.colorScheme.tertiary; Level.ADVANCED -> MaterialTheme.colorScheme.error }
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Surface(color = lc.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) { Text(item.level.label, Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = lc) }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(item.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                Row { item.tags.take(3).forEach { tag -> Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.padding(end = 4.dp)) { Text(tag, Modifier.padding(horizontal = 4.dp, vertical = 1.dp), style = MaterialTheme.typography.labelSmall) } } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: Category, onBack: () -> Unit, onItemClick: (KnowledgeItem) -> Unit) {
    val items = remember(category) { KnowledgeBase.getByCategory(category) }
    Scaffold(topBar = { TopAppBar(title = { Text(category.label) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Level.entries.forEach { lvl ->
                val lvlItems = items.filter { it.level == lvl }
                if (lvlItems.isNotEmpty()) {
                    item { Text("${lvl.label} (${lvlItems.size}篇)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
                    items(lvlItems) { KnowledgeCard(it) { onItemClick(it) } }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(item: KnowledgeItem, allItems: List<KnowledgeItem>, onBack: () -> Unit, onItemClick: (KnowledgeItem) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(item.title, maxLines = 1) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val lc = when (item.level) { Level.BEGINNER -> MaterialTheme.colorScheme.primary; Level.INTERMEDIATE -> MaterialTheme.colorScheme.tertiary; Level.ADVANCED -> MaterialTheme.colorScheme.error }
                Surface(color = lc.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) { Text(item.level.label, Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = lc, style = MaterialTheme.typography.labelSmall) }
                Spacer(Modifier.width(8.dp))
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) { Text(item.category.label, Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
            }
            Spacer(Modifier.height(12.dp))
            if (item.imageType != ImageType.NONE) {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(8.dp)) {
                        Text("图解", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        when {
                            item.id.contains("kline_") || item.id == "basic_004" -> PatternDiagram(
                                when {
                                    item.id == "kline_001" -> "hammer"
                                    item.id == "kline_002" -> "engulfing"
                                    item.id == "kline_003" -> "morning_star"
                                    item.id == "kline_004" -> "head_shoulders"
                                    item.id == "kline_005" -> "double_top"
                                    item.id == "kline_006" -> "triangle"
                                    else -> "hammer"
                                }
                            )
                            item.imageType == ImageType.CHART -> SingleCandlestick(CandlestickData(100f, 108f, 96f, 105f), annotations = listOf("最高108" to 0.95f, "收105" to 0.6f, "开100" to 0.4f, "最低96" to 0.05f))
                            else -> PatternDiagram("doji")
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            Text(item.summary, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            item.content.split("\n").forEach { line ->
                when {
                    line.startsWith("**") && line.contains("**") -> {
                        val parts = line.split("**").filter { it.isNotBlank() }
                        if (parts.size >= 2) {
                            val bold = parts.first().trim()
                            val rest = parts.drop(1).joinToString("").trim()
                            Text(buildString { append(bold); if (rest.isNotEmpty()) append(" $rest") }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        } else Text(line, style = MaterialTheme.typography.bodyMedium)
                    }
                    line.startsWith("- ") -> Text(line, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp))
                    line.startsWith("> ") -> Text(line.removePrefix("> "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                    line.startsWith("#") -> Text(line.removePrefix("#").trim(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    line.isBlank() -> Spacer(Modifier.height(4.dp))
                    else -> Text(line, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                item.tags.forEach { tag ->
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) { Text(tag, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall) }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (item.relatedIds.isNotEmpty()) {
                Text("相关知识点", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                val related = item.relatedIds.mapNotNull { rid -> allItems.find { it.id == rid } }
                related.forEach { r -> KnowledgeCard(r) { onItemClick(r) } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit, onItemClick: (KnowledgeItem) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { KnowledgeBase.search(query) }
    Scaffold(topBar = {
        TopAppBar(title = { TextField(value = query, onValueChange = { query = it }, placeholder = { Text("搜索知识点、术语、指标...") }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f), unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f), focusedTextColor = MaterialTheme.colorScheme.onPrimary, unfocusedTextColor = MaterialTheme.colorScheme.onPrimary, cursorColor = MaterialTheme.colorScheme.onPrimary), modifier = Modifier.fillMaxWidth()) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary))
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (query.isBlank()) {
                item { Text("输入关键词搜索", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 32.dp)) }
                item { Text("热门搜索", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
                items(listOf("MACD", "K线", "止损", "期货", "PE", "背离", "趋势", "成交量")) { tag ->
                    Surface(onClick = { query = tag }, color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small, modifier = Modifier.clickable { query = tag }) { Text(tag, Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.bodyMedium) }
                }
            } else if (results.isEmpty()) {
                item { Text("未找到\"$query\"相关内容", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 32.dp)) }
            } else {
                item { Text("找到 ${results.size} 个结果", style = MaterialTheme.typography.labelMedium) }
                items(results) { KnowledgeCard(it) { onItemClick(it) } }
            }
        }
    }
}
