package com.example.creatorgrowthos.ui.screens.extra

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.*
import com.example.creatorgrowthos.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ExtraModuleTab(val label: String, val routeKey: String) {
    PILLARS("Pillars", "content_pillars"),
    TEMPLATES("Templates", "content_templates"),
    RESEARCH("Research", "research_library"),
    BRAND_ASSETS("Brand Assets", "brand_assets"),
    PRODUCT_IDEAS("Product Ideas", "product_ideas"),
    MONETIZATION("Monetization", "monetization"),
    REVIEWS("Reviews", "reviews")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtrasScreen(
    initialRouteKey: String?,
    viewModel: ExtrasViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember {
        mutableStateOf(
            ExtraModuleTab.values().find { it.routeKey == initialRouteKey } ?: ExtraModuleTab.PILLARS
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Slate200
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "CREATOR SYSTEM",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedTab.label,
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }
            }

            IconButton(
                onClick = { showAddDialog = true },
                colors = IconButtonDefaults.iconButtonColors(containerColor = ElectricBluePrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Slate950
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Category Switcher
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ExtraModuleTab.values()) { tab ->
                val isSelected = tab == selectedTab
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    label = { Text(tab.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBluePrimary,
                        selectedLabelColor = Slate950,
                        containerColor = Slate850,
                        labelColor = Slate300
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) ElectricBluePrimary else Slate800
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content for selected tab
        when (selectedTab) {
            ExtraModuleTab.PILLARS -> {
                PillarsList(
                    pillars = uiState.pillars,
                    onDelete = { viewModel.deletePillar(it) }
                )
            }
            ExtraModuleTab.TEMPLATES -> {
                TemplatesList(templates = uiState.templates)
            }
            ExtraModuleTab.RESEARCH -> {
                ResearchList(
                    research = uiState.researchResources,
                    onDelete = { viewModel.deleteResearch(it) }
                )
            }
            ExtraModuleTab.BRAND_ASSETS -> {
                BrandAssetsList(
                    assets = uiState.brandAssets,
                    onDelete = { viewModel.deleteBrandAsset(it) }
                )
            }
            ExtraModuleTab.PRODUCT_IDEAS -> {
                ProductIdeasList(
                    products = uiState.productIdeas,
                    onDelete = { viewModel.deleteProductIdea(it) }
                )
            }
            ExtraModuleTab.MONETIZATION -> {
                MonetizationList(
                    records = uiState.monetizationRecords,
                    totalEarnings = uiState.totalEarnings,
                    onDelete = { viewModel.deleteMonetizationRecord(it) }
                )
            }
            ExtraModuleTab.REVIEWS -> {
                ReviewsList(reviews = uiState.reviews)
            }
        }
    }

    // Add dialog depending on tab
    if (showAddDialog) {
        when (selectedTab) {
            ExtraModuleTab.PILLARS -> {
                AddPillarDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { name, desc, targetPct, colorHex ->
                        viewModel.addPillar(
                            ContentPillarEntity(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                description = desc,
                                targetPercentage = targetPct,
                                colorHex = colorHex
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.TEMPLATES -> {
                AddTemplateDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { name, format, body ->
                        viewModel.addTemplate(
                            ContentTemplateEntity(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                format = format,
                                structureOutline = body
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.RESEARCH -> {
                AddResearchDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { title, category, url, summary, takeaways ->
                        viewModel.addResearch(
                            ResearchResourceEntity(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                category = category,
                                url = url,
                                summary = summary,
                                keyTakeaways = takeaways
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.BRAND_ASSETS -> {
                AddBrandAssetDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { type, assetName, assetValue, desc ->
                        viewModel.addBrandAsset(
                            BrandAssetEntity(
                                id = UUID.randomUUID().toString(),
                                assetName = assetName,
                                assetType = type,
                                assetValue = assetValue,
                                description = desc
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.PRODUCT_IDEAS -> {
                AddProductIdeaDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { title, type, audience, price, notes ->
                        viewModel.addProductIdea(
                            ProductIdeaEntity(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                productType = type,
                                targetAudience = audience,
                                priceEstimate = price,
                                validationStatus = "VALIDATING",
                                notes = notes
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.MONETIZATION -> {
                AddMonetizationDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { sourceName, amount, notes ->
                        viewModel.addMonetizationRecord(
                            MonetizationRecordEntity(
                                id = UUID.randomUUID().toString(),
                                sourceName = sourceName,
                                amount = amount,
                                currency = "USD",
                                dateUtc = System.currentTimeMillis(),
                                notes = notes
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
            ExtraModuleTab.REVIEWS -> {
                AddReviewDialog(
                    onDismiss = { showAddDialog = false },
                    onSave = { type, win, bottleneck, lesson, rating ->
                        viewModel.addReview(
                            ReviewEntity(
                                id = UUID.randomUUID().toString(),
                                reviewType = type,
                                periodString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                timestampUtc = System.currentTimeMillis(),
                                accomplishments = win,
                                bottlenecks = bottleneck,
                                learnings = lesson,
                                ratingScore = rating
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

// ----------------- SUB-COMPONENTS & LISTS -----------------

@Composable
private fun PillarsList(pillars: List<ContentPillarEntity>, onDelete: (String) -> Unit) {
    if (pillars.isEmpty()) {
        EmptyExtraState("No Content Pillars yet", "Create core strategic topics to balance your content mix.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(pillars) { pillar ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = try {
                                Color(android.graphics.Color.parseColor(pillar.colorHex.ifBlank { "#00E5FF" }))
                            } catch (e: Exception) {
                                ElectricBluePrimary
                            },
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pillar.name, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            Text(pillar.description, color = Slate400, style = Typography.bodySmall)
                            Text("Target allocation: ${pillar.targetPercentage}%", color = MintSuccessLight, style = Typography.labelSmall)
                        }
                        IconButton(onClick = { onDelete(pillar.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplatesList(templates: List<ContentTemplateEntity>) {
    if (templates.isEmpty()) {
        EmptyExtraState("No Templates Saved", "Create reusable script frameworks or production automation checklists.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(templates) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(template.name, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Slate800
                            ) {
                                Text(
                                    template.format,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = Typography.labelSmall,
                                    color = ElectricBlueLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(template.structureOutline, color = Slate300, style = Typography.bodySmall, maxLines = 5)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResearchList(research: List<ResearchResourceEntity>, onDelete: (String) -> Unit) {
    if (research.isEmpty()) {
        EmptyExtraState("Research Library Empty", "Save viral hooks, competitor insights, and study references.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(research) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.title, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            IconButton(onClick = { onDelete(item.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                            }
                        }
                        if (item.category.isNotBlank()) {
                            Text("Category: ${item.category}", color = ElectricBlueLight, style = Typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.summary, color = Slate300, style = Typography.bodySmall)
                        if (item.url.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.url, color = Slate400, style = Typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandAssetsList(assets: List<BrandAssetEntity>, onDelete: (String) -> Unit) {
    if (assets.isEmpty()) {
        EmptyExtraState("No Brand Assets", "Store hex codes, fonts, tone guidelines, and media links.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(assets) { asset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Slate800) {
                                Text(
                                    asset.assetType,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = Typography.labelSmall,
                                    color = ElectricBlueLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(asset.assetName, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            Text(asset.assetValue, color = MintSuccessLight, style = Typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            if (asset.description.isNotBlank()) {
                                Text(asset.description, color = Slate400, style = Typography.labelSmall)
                            }
                        }
                        IconButton(onClick = { onDelete(asset.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductIdeasList(products: List<ProductIdeaEntity>, onDelete: (String) -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    if (products.isEmpty()) {
        EmptyExtraState("No Product Ideas", "Brainstorm digital products, courses, mentorship, or merchandise.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(products) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.title, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Slate800) {
                                    Text(
                                        item.productType,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = Typography.labelSmall,
                                        color = ElectricBlueLight
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Est. Price: ${currencyFormat.format(item.priceEstimate)} • Audience: ${item.targetAudience.ifBlank { "All" }}",
                                color = MintSuccessLight,
                                style = Typography.bodySmall
                            )
                            Text("Status: ${item.validationStatus}", color = Slate400, style = Typography.labelSmall)
                        }
                        IconButton(onClick = { onDelete(item.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonetizationList(records: List<MonetizationRecordEntity>, totalEarnings: Double, onDelete: (String) -> Unit) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TOTAL RECORDED EARNINGS", style = Typography.labelSmall, color = Slate400)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        currencyFormat.format(totalEarnings),
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MintSuccessLight
                    )
                }
            }
        }

        if (records.isEmpty()) {
            item {
                EmptyExtraState("No Monetization Records", "Track AdSense, Brand Deals, Affiliate Commissions, and Course Sales.")
            }
        } else {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(record.sourceName, fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${currencyFormat.format(record.amount)} • ${dateFormat.format(Date(record.dateUtc))}",
                                color = MintSuccessLight,
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (record.notes.isNotBlank()) {
                                Text(record.notes, color = Slate400, style = Typography.labelSmall)
                            }
                        }
                        IconButton(onClick = { onDelete(record.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsList(reviews: List<ReviewEntity>) {
    if (reviews.isEmpty()) {
        EmptyExtraState("No Reviews Logged", "Perform daily debriefs and weekly strategic performance retrospectives.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reviews) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${item.reviewType} REVIEW (${item.periodString})", fontWeight = FontWeight.Bold, color = Slate100, style = Typography.titleMedium)
                            Surface(shape = RoundedCornerShape(4.dp), color = Slate800) {
                                Text(
                                    "Score: ${item.ratingScore}/10",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = Typography.labelSmall,
                                    color = MintSuccessLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Accomplishments: ${item.accomplishments}", color = Slate300, style = Typography.bodySmall)
                        Text("Bottlenecks: ${item.bottlenecks}", color = AmberWarningLight, style = Typography.bodySmall)
                        Text("Learnings: ${item.learnings}", color = ElectricBlueLight, style = Typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyExtraState(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, contentDescription = null, tint = Slate600, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Slate300, style = Typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Slate500, style = Typography.bodySmall, modifier = Modifier.padding(horizontal = 24.dp))
        }
    }
}

// ----------------- DIALOGS -----------------

@Composable
private fun AddPillarDialog(onDismiss: () -> Unit, onSave: (String, String, Float, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var targetPct by remember { mutableStateOf("25") }
    var colorHex by remember { mutableStateOf("#00E5FF") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Content Pillar", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Pillar Name") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Strategic Objective") })
                OutlinedTextField(value = targetPct, onValueChange = { targetPct = it }, label = { Text("Target Allocation (%)") })
                OutlinedTextField(value = colorHex, onValueChange = { colorHex = it }, label = { Text("Color Hex (e.g. #00E5FF)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onSave(name, desc, targetPct.toFloatOrNull() ?: 25f, colorHex)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddTemplateDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("SHORT_FORM") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Content Template", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Template Name") })
                OutlinedTextField(value = format, onValueChange = { format = it }, label = { Text("Format (SHORT_FORM / LONG_FORM_YOUTUBE)") })
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Structure Outline") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, format, body) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddResearchDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, List<String>) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("COMPETITOR_ANALYSIS") }
    var url by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Research Resource", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title / Source Name") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL Link") })
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Summary & Insights") }, minLines = 2)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onSave(title, category, url, summary, listOf(summary)) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddBrandAssetDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var type by remember { mutableStateOf("COLOR_PALETTE") }
    var assetName by remember { mutableStateOf("") }
    var assetValue by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Brand Asset", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (COLOR_PALETTE/TYPOGRAPHY/BRAND_VOICE)") })
                OutlinedTextField(value = assetName, onValueChange = { assetName = it }, label = { Text("Asset Name (e.g. Primary Blue)") })
                OutlinedTextField(value = assetValue, onValueChange = { assetValue = it }, label = { Text("Asset Value (e.g. #00E5FF or Inter)") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description / Guidelines") })
            }
        },
        confirmButton = {
            Button(
                onClick = { if (assetName.isNotBlank()) onSave(type, assetName, assetValue, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddProductIdeaDialog(onDismiss: () -> Unit, onSave: (String, String, String, Double, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("DIGITAL_PRODUCT") }
    var audience by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("49.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Product / Offer", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") })
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (COURSE/DIGITAL_PRODUCT/COMMUNITY)") })
                OutlinedTextField(value = audience, onValueChange = { audience = it }, label = { Text("Target Audience") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price Estimate ($)") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Strategic Notes") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, type, audience, price.toDoubleOrNull() ?: 0.0, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddMonetizationDialog(onDismiss: () -> Unit, onSave: (String, Double, String) -> Unit) {
    var sourceName by remember { mutableStateOf("SPONSORSHIP") }
    var amount by remember { mutableStateOf("500.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Revenue", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = sourceName, onValueChange = { sourceName = it }, label = { Text("Source (AD_REVENUE/SPONSORSHIP/AFFILIATE)") })
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ($)") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / Deal Details") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sourceName.isNotBlank()) {
                        onSave(sourceName, amount.toDoubleOrNull() ?: 0.0, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
private fun AddReviewDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, Int) -> Unit) {
    var type by remember { mutableStateOf("DAILY") }
    var wins by remember { mutableStateOf("") }
    var bottlenecks by remember { mutableStateOf("") }
    var lessons by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("8") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Creator Review", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (DAILY / WEEKLY)") })
                OutlinedTextField(value = wins, onValueChange = { wins = it }, label = { Text("Accomplishments / Wins") })
                OutlinedTextField(value = bottlenecks, onValueChange = { bottlenecks = it }, label = { Text("Bottlenecks / Distractions") })
                OutlinedTextField(value = lessons, onValueChange = { lessons = it }, label = { Text("Key Learnings") })
                OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Rating Score (1-10)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (wins.isNotBlank()) {
                        onSave(type, wins, bottlenecks, lessons, rating.toIntOrNull() ?: 8)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}
