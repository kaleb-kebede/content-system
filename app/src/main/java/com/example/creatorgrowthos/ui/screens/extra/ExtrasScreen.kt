package com.example.creatorgrowthos.ui.screens.extra

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class ExtraModuleTab(val label: String, val routeKey: String, val icon: ImageVector) {
    PILLARS("Pillars", "content_pillars", Icons.Default.Category),
    TEMPLATES("Templates", "content_templates", Icons.Default.Description),
    RESEARCH("Research", "research_library", Icons.Default.Search),
    BRAND_ASSETS("Brand Assets", "brand_assets", Icons.Default.Palette),
    PRODUCT_IDEAS("Product Ideas", "product_ideas", Icons.Default.Lightbulb),
    MONETIZATION("Monetization", "monetization", Icons.Default.AttachMoney),
    REVIEWS("Reviews", "reviews", Icons.Default.RateReview)
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

        // Modern Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate850,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate200,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "CREATOR REPOSITORY",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.4.sp,
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

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElectricBluePrimary,
                modifier = Modifier.size(40.dp)
            ) {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New Item",
                        tint = Slate950,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Category Switcher Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ExtraModuleTab.values()) { tab ->
                val isSelected = tab == selectedTab
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ElectricBluePrimary else Slate850),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) ElectricBluePrimary else BorderSubtle
                    )
                ) {
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        leadingIcon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) Slate950 else Slate400
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = Typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBluePrimary,
                            selectedLabelColor = Slate950,
                            containerColor = Slate850,
                            labelColor = Slate300
                        ),
                        border = null
                    )
                }
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
        EmptyExtraState(
            icon = Icons.Default.Category,
            title = "No Content Pillars Defined",
            subtitle = "Formulate core strategic pillars to balance educational, authority, and viral content distribution."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(pillars, key = { it.id }) { pillar ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val parsedColor = try {
                            Color(android.graphics.Color.parseColor(pillar.colorHex.ifBlank { "#38BDF8" }))
                        } catch (e: Exception) {
                            ElectricBluePrimary
                        }

                        Surface(
                            shape = CircleShape,
                            color = parsedColor,
                            modifier = Modifier.size(16.dp)
                        ) {}

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = pillar.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    style = Typography.titleMedium
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Slate800,
                                    border = BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text(
                                        text = "${pillar.targetPercentage.toInt()}% Target",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = Typography.labelSmall,
                                        color = MintProgress,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (pillar.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pillar.description,
                                    color = Slate400,
                                    style = Typography.bodySmall
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDelete(pillar.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Pillar",
                                tint = Slate400,
                                modifier = Modifier.size(20.dp)
                            )
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
        EmptyExtraState(
            icon = Icons.Default.Description,
            title = "No Script Templates Saved",
            subtitle = "Create reusable script frameworks, 3-act story models, or viral hook formulas."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(templates, key = { it.id }) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.name,
                                fontWeight = FontWeight.Bold,
                                color = Slate100,
                                style = Typography.titleMedium
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate800,
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = template.format,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = Typography.labelSmall,
                                    color = ElectricBlueLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = template.structureOutline,
                            color = Slate300,
                            style = Typography.bodySmall,
                            lineHeight = 18.sp,
                            maxLines = 6
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResearchList(research: List<ResearchResourceEntity>, onDelete: (String) -> Unit) {
    if (research.isEmpty()) {
        EmptyExtraState(
            icon = Icons.Default.Search,
            title = "Research Vault Empty",
            subtitle = "Bookmark viral references, case studies, competitor breakdowns, and trend analyses."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(research, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    style = Typography.titleMedium
                                )
                                if (item.category.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.category,
                                        color = PurpleLight,
                                        style = Typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onDelete(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Resource",
                                    tint = Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.summary,
                            color = Slate300,
                            style = Typography.bodySmall,
                            lineHeight = 18.sp
                        )
                        if (item.url.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate800,
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = item.url,
                                    color = ElectricBlueLight,
                                    style = Typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1
                                )
                            }
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
        EmptyExtraState(
            icon = Icons.Default.Palette,
            title = "No Brand Assets Configured",
            subtitle = "Store primary color palettes, typography specs, brand voice rules, and signature hooks."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(assets, key = { it.id }) { asset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate800,
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = asset.assetType,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = Typography.labelSmall,
                                    color = ElectricBlueLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = asset.assetName,
                                fontWeight = FontWeight.Bold,
                                color = Slate100,
                                style = Typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = asset.assetValue,
                                color = MintProgress,
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (asset.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = asset.description,
                                    color = Slate400,
                                    style = Typography.bodySmall
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDelete(asset.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Brand Asset",
                                tint = Slate400,
                                modifier = Modifier.size(20.dp)
                            )
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
        EmptyExtraState(
            icon = Icons.Default.Lightbulb,
            title = "Product Idea Pipeline Empty",
            subtitle = "Brainstorm digital products, courses, private communities, and high-ticket consulting offers."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(products, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    style = Typography.titleMedium
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Slate800,
                                    border = BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text(
                                        text = item.productType,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = Typography.labelSmall,
                                        color = ElectricBlueLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Est. Price: ${currencyFormat.format(item.priceEstimate)} • Audience: ${item.targetAudience.ifBlank { "All Audience" }}",
                                color = MintProgress,
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.notes,
                                    color = Slate400,
                                    style = Typography.bodySmall
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDelete(item.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Product Idea",
                                tint = Slate400,
                                modifier = Modifier.size(20.dp)
                            )
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
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MintProgress.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GROSS REVENUE RECORDED",
                            style = Typography.labelSmall,
                            color = Slate400,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = CircleShape,
                            color = MintProgress.copy(alpha = 0.16f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = MintProgress,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currencyFormat.format(totalEarnings),
                        style = Typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MintProgress
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Aggregated earnings across AdSense, brand integrations, and digital products.",
                        style = Typography.bodySmall,
                        color = Slate400
                    )
                }
            }
        }

        if (records.isEmpty()) {
            item {
                EmptyExtraState(
                    icon = Icons.Default.AttachMoney,
                    title = "No Revenue Entries Recorded",
                    subtitle = "Log sponsorships, affiliate commissions, ad revenue disbursements, and digital product sales."
                )
            }
        } else {
            items(records, key = { it.id }) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = record.sourceName,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    style = Typography.titleMedium
                                )
                                Text(
                                    text = dateFormat.format(Date(record.dateUtc)),
                                    color = Slate400,
                                    style = Typography.labelSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currencyFormat.format(record.amount),
                                color = MintProgress,
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (record.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = record.notes,
                                    color = Slate400,
                                    style = Typography.bodySmall
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDelete(record.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Monetization Entry",
                                tint = Slate400,
                                modifier = Modifier.size(20.dp)
                            )
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
        EmptyExtraState(
            icon = Icons.Default.RateReview,
            title = "No Creator Reviews Logged",
            subtitle = "Perform end-of-day debriefs and weekly strategic retrospectives to eliminate operational friction."
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reviews, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.reviewType} DEBRIEF (${item.periodString})",
                                fontWeight = FontWeight.Bold,
                                color = Slate100,
                                style = Typography.titleMedium
                            )

                            val scoreColor = when {
                                item.ratingScore >= 8 -> MintProgress
                                item.ratingScore >= 5 -> AmberWarning
                                else -> CrimsonCritical
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = scoreColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = "Score: ${item.ratingScore}/10",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = Typography.labelSmall,
                                    color = scoreColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (item.accomplishments.isNotBlank()) {
                            ReviewBulletItem(
                                label = "Accomplishments",
                                text = item.accomplishments,
                                tint = MintProgress
                            )
                        }

                        if (item.bottlenecks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            ReviewBulletItem(
                                label = "Bottlenecks",
                                text = item.bottlenecks,
                                tint = AmberWarning
                            )
                        }

                        if (item.learnings.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            ReviewBulletItem(
                                label = "Learnings",
                                text = item.learnings,
                                tint = ElectricBlueLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewBulletItem(
    label: String,
    text: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = Typography.labelSmall,
                color = tint,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text,
                style = Typography.bodySmall,
                color = Slate200,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun EmptyExtraState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(16.dp),
        color = Slate850,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Slate800,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Slate100,
                style = Typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Slate400,
                style = Typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// ----------------- DIALOGS -----------------

@Composable
private fun AddPillarDialog(onDismiss: () -> Unit, onSave: (String, String, Float, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var targetPct by remember { mutableStateOf("25") }
    var colorHex by remember { mutableStateOf("#38BDF8") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Content Pillar", color = Slate100, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Pillar Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Strategic Objective") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = targetPct,
                    onValueChange = { targetPct = it },
                    label = { Text("Target Allocation (%)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = colorHex,
                    onValueChange = { colorHex = it },
                    label = { Text("Color Hex (e.g. #38BDF8)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onSave(name, desc, targetPct.toFloatOrNull() ?: 25f, colorHex)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Pillar", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = format,
                    onValueChange = { format = it },
                    label = { Text("Format (SHORT_FORM / LONG_FORM_YOUTUBE)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Structure Outline") },
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, format, body) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Template", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Source Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL Link") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary & Key Takeaways") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onSave(title, category, url, summary, listOf(summary)) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Resource", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (COLOR_PALETTE/TYPOGRAPHY/BRAND_VOICE)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = assetName,
                    onValueChange = { assetName = it },
                    label = { Text("Asset Name (e.g. Primary Blue)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = assetValue,
                    onValueChange = { assetValue = it },
                    label = { Text("Asset Value (e.g. #38BDF8 or Inter)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description / Brand Voice Rules") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (assetName.isNotBlank()) onSave(type, assetName, assetValue, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Asset", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (COURSE/DIGITAL_PRODUCT/COMMUNITY)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = audience,
                    onValueChange = { audience = it },
                    label = { Text("Target Audience") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price Estimate ($)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Strategic Notes") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, type, audience, price.toDoubleOrNull() ?: 0.0, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Product", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = sourceName,
                    onValueChange = { sourceName = it },
                    label = { Text("Source (AD_REVENUE/SPONSORSHIP/AFFILIATE)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount ($)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Deal Notes / Brand Details") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sourceName.isNotBlank()) {
                        onSave(sourceName, amount.toDoubleOrNull() ?: 0.0, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Revenue", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (DAILY / WEEKLY)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = wins,
                    onValueChange = { wins = it },
                    label = { Text("Accomplishments / Wins") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = bottlenecks,
                    onValueChange = { bottlenecks = it },
                    label = { Text("Bottlenecks / Distractions") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = lessons,
                    onValueChange = { lessons = it },
                    label = { Text("Key Learnings") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating Score (1-10)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedLabelColor = ElectricBlueLight,
                        unfocusedLabelColor = Slate400,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate200
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (wins.isNotBlank()) {
                        onSave(type, wins, bottlenecks, lessons, rating.toIntOrNull() ?: 8)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Review", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(24.dp)
    )
}
