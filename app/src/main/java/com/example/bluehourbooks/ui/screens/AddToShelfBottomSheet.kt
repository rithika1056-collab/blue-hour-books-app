package com.example.bluehourbooks.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehourbooks.data.model.DecorationPresets
import com.example.bluehourbooks.data.model.DecorationStylePreset
import com.example.bluehourbooks.data.model.ShelfDecoration
import com.example.bluehourbooks.ui.theme.Cream50
import com.example.bluehourbooks.ui.theme.Gold400
import com.example.bluehourbooks.ui.theme.Lavender200
import com.example.bluehourbooks.ui.theme.Lavender300
import com.example.bluehourbooks.ui.theme.Lavender500
import com.example.bluehourbooks.ui.theme.Lavender700
import com.example.bluehourbooks.ui.theme.Midnight100
import com.example.bluehourbooks.ui.theme.Midnight200
import com.example.bluehourbooks.ui.theme.Midnight600
import com.example.bluehourbooks.ui.theme.Midnight700
import com.example.bluehourbooks.ui.theme.Midnight800
import com.example.bluehourbooks.ui.theme.Midnight900
import com.example.bluehourbooks.ui.theme.Midnight950

private data class AddOptionItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Brush,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToShelfBottomSheet(
    onDismiss: () -> Unit,
    onSelectAddBooks: () -> Unit,
    onSelectDecorationType: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val options = listOf(
        AddOptionItem(
            id = "BOOK",
            title = "Add Books",
            description = "Search or manually log a finished read for your shelf",
            icon = Icons.Filled.AutoStories,
            iconBg = Brush.linearGradient(listOf(Lavender500, Lavender700)),
            tag = "add_option_books"
        ),
        AddOptionItem(
            id = "PLANT",
            title = "Add Plant",
            description = "Potted succulents, peace lilies, and cozy greenery",
            icon = Icons.Filled.Spa,
            iconBg = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF064E3B))),
            tag = "add_option_plant"
        ),
        AddOptionItem(
            id = "FRAME",
            title = "Add Photo Frame",
            description = "Landscape canvas art and framed bookish prints",
            icon = Icons.Filled.Image,
            iconBg = Brush.linearGradient(listOf(Gold400, Color(0xFF78350F))),
            tag = "add_option_frame"
        ),
        AddOptionItem(
            id = "QUOTE",
            title = "Add Quote Card",
            description = "Inspirational bookish quote cards and plaques",
            icon = Icons.Filled.FormatQuote,
            iconBg = Brush.linearGradient(listOf(Lavender300, Midnight600)),
            tag = "add_option_quote"
        ),
        AddOptionItem(
            id = "CURIO",
            title = "Add Study Curio",
            description = "Brass desk lamps, hourglass sand timers, and crystals",
            icon = Icons.Filled.AutoAwesome,
            iconBg = Brush.linearGradient(listOf(Gold400, Lavender500)),
            tag = "add_option_curio"
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Midnight900,
        contentColor = Cream50,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add to Shelf",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = Cream50
                    )
                    Text(
                        text = "Personalize your study with books & cozy decor",
                        fontSize = 12.5.sp,
                        color = Lavender200.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Midnight800)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Midnight100,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(item.tag)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Midnight800.copy(alpha = 0.6f))
                            .border(1.dp, Midnight700.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .clickable {
                                onDismiss()
                                if (item.id == "BOOK") {
                                    onSelectAddBooks()
                                } else {
                                    onSelectDecorationType(item.id)
                                }
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Cream50,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Cream50
                            )
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Midnight100.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecorationPickerBottomSheet(
    decorationType: String,
    totalShelves: Int,
    currentShelfIndex: Int,
    onDismiss: () -> Unit,
    onAddDecoration: (ShelfDecoration) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presets = remember(decorationType) {
        DecorationPresets.getPresetsForType(decorationType)
    }

    var selectedPreset by remember { mutableStateOf(presets.firstOrNull()) }
    var selectedShelf by remember { mutableIntStateOf(currentShelfIndex) }
    var selectedPosition by remember { mutableIntStateOf(1) } // 0=Left, 1=Center, 2=Right
    var customQuoteText by remember { mutableStateOf(selectedPreset?.quoteText ?: "") }
    var customQuoteAuthor by remember { mutableStateOf(selectedPreset?.quoteAuthor ?: "") }

    val typeLabel = when (decorationType.uppercase()) {
        "PLANT" -> "Select a Plant"
        "FRAME" -> "Select a Frame"
        "QUOTE" -> "Choose an Inspirational Quote"
        "CURIO" -> "Select a Study Curio"
        else -> "Select Decoration"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Midnight900,
        contentColor = Cream50,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = typeLabel,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Cream50
                    )
                    Text(
                        text = "Choose a style to sit gracefully on your shelf",
                        fontSize = 12.sp,
                        color = Lavender200.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Midnight800)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Midnight100,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets Grid / List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(presets) { preset ->
                    val isSelected = selectedPreset?.styleKey == preset.styleKey
                    val borderCol by animateColorAsState(
                        targetValue = if (isSelected) Lavender500 else Midnight700.copy(alpha = 0.5f),
                        label = "preset_border"
                    )
                    val bgCol by animateColorAsState(
                        targetValue = if (isSelected) Lavender500.copy(alpha = 0.2f) else Midnight800.copy(alpha = 0.5f),
                        label = "preset_bg"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preset_${preset.styleKey}")
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgCol)
                            .border(1.dp, borderCol, RoundedCornerShape(14.dp))
                            .clickable {
                                selectedPreset = preset
                                if (preset.quoteText != null) customQuoteText = preset.quoteText
                                if (preset.quoteAuthor != null) customQuoteAuthor = preset.quoteAuthor
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = preset.iconEmoji, fontSize = 26.sp)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Cream50 else Midnight100
                            )
                            Text(
                                text = preset.description,
                                fontSize = 11.5.sp,
                                color = Lavender200.copy(alpha = 0.7f)
                            )
                            if (preset.quoteText != null) {
                                Text(
                                    text = "“${preset.quoteText}”",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Gold400.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Custom Quote fields if Quote type
            if (decorationType.uppercase() == "QUOTE") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customQuoteText,
                    onValueChange = { customQuoteText = it },
                    label = { Text("Custom Quote", fontSize = 12.sp) },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Midnight800,
                        unfocusedContainerColor = Midnight800.copy(alpha = 0.6f),
                        focusedBorderColor = Lavender300,
                        unfocusedBorderColor = Midnight600,
                        focusedTextColor = Cream50,
                        unfocusedTextColor = Cream50
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Placement settings (Shelf & Position)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shelf Selection Chips
                Column {
                    Text("Target Shelf", fontSize = 11.5.sp, color = Lavender200.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (s in 0 until maxOf(totalShelves, 2)) {
                            val isSel = selectedShelf == s
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Lavender500 else Midnight800)
                                    .border(1.dp, if (isSel) Lavender300 else Midnight600, RoundedCornerShape(8.dp))
                                    .clickable { selectedShelf = s }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Shelf ${s + 1}",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Cream50 else Midnight100
                                )
                            }
                        }
                    }
                }

                // Position on shelf
                Column {
                    Text("Shelf Slot", fontSize = 11.5.sp, color = Lavender200.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "Left", 1 to "Center", 2 to "Right").forEach { (pos, label) ->
                            val isSel = selectedPosition == pos
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Lavender500 else Midnight800)
                                    .border(1.dp, if (isSel) Lavender300 else Midnight600, RoundedCornerShape(8.dp))
                                    .clickable { selectedPosition = pos }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Cream50 else Midnight100
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Add Button
            Button(
                onClick = {
                    val preset = selectedPreset ?: return@Button
                    val decoration = ShelfDecoration(
                        shelfIndex = selectedShelf,
                        type = decorationType.uppercase(),
                        styleKey = preset.styleKey,
                        title = preset.name,
                        subtitle = if (decorationType.uppercase() == "QUOTE") customQuoteText else preset.description,
                        position = selectedPosition
                    )
                    onAddDecoration(decoration)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lavender500,
                    contentColor = Cream50
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_add_decoration_button")
            ) {
                Text(
                    text = "Place on Shelf",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDecorationBottomSheet(
    decoration: ShelfDecoration,
    totalShelves: Int,
    onDismiss: () -> Unit,
    onUpdate: (ShelfDecoration) -> Unit,
    onDelete: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedShelf by remember { mutableIntStateOf(decoration.shelfIndex) }
    var selectedPosition by remember { mutableIntStateOf(decoration.position) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Midnight900,
        contentColor = Cream50,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = decoration.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Cream50
                    )
                    Text(
                        text = "${decoration.type.lowercase().replaceFirstChar { it.uppercase() }} decoration on Shelf ${decoration.shelfIndex + 1}",
                        fontSize = 12.sp,
                        color = Lavender200.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Midnight800)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Midnight100,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!decoration.subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Midnight800.copy(alpha = 0.5f))
                        .border(1.dp, Midnight700.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = decoration.subtitle,
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Cream50.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Move Shelf & Position Controls
            Text("Shelf Position", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Cream50)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shelf selector
                Column {
                    Text("Shelf Number", fontSize = 11.sp, color = Lavender200.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (s in 0 until maxOf(totalShelves, 2)) {
                            val isSel = selectedShelf == s
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Lavender500 else Midnight800)
                                    .border(1.dp, if (isSel) Lavender300 else Midnight600, RoundedCornerShape(8.dp))
                                    .clickable { selectedShelf = s }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Shelf ${s + 1}",
                                    fontSize = 11.sp,
                                    color = if (isSel) Cream50 else Midnight100
                                )
                            }
                        }
                    }
                }

                // Slot selector
                Column {
                    Text("Slot", fontSize = 11.sp, color = Lavender200.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "Left", 1 to "Center", 2 to "Right").forEach { (pos, label) ->
                            val isSel = selectedPosition == pos
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Lavender500 else Midnight800)
                                    .border(1.dp, if (isSel) Lavender300 else Midnight600, RoundedCornerShape(8.dp))
                                    .clickable { selectedPosition = pos }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSel) Cream50 else Midnight100
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Save & Remove)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDelete(decoration.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                        contentColor = Color(0xFFFCA5A5)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("delete_decoration_button")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        onUpdate(decoration.copy(shelfIndex = selectedShelf, position = selectedPosition))
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Lavender500,
                        contentColor = Cream50
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(46.dp)
                        .testTag("save_decoration_button")
                ) {
                    Text("Save Changes", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
