package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BackgroundColor = Color(0xFFF2F2F7)
private val SecondaryText = Color(0xFF8E8E93)
private val AppleYellow = Color(0xFFFFCC00)

data class Note(
    val id: Int,
    val title: String,
    val time: String,
    val content: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NotesScreen()
            }
        }
    }
}

@Composable
fun NotesScreen() {
    val pinnedNotes = remember {
        mutableStateListOf(
            Note(1, "需要带的物品", "05:44", "水")
        )
    }

    val todayNotes = remember {
        mutableStateListOf(
            Note(2, "景观设计", "05:45", "景观用于铺设后廊的石板")
        )
    }

    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var nextId by remember { mutableIntStateOf(3) }

    val filteredPinned = pinnedNotes.filter {
        it.title.contains(searchText, true) ||
                it.content.contains(searchText, true)
    }

    val filteredToday = todayNotes.filter {
        it.title.contains(searchText, true) ||
                it.content.contains(searchText, true)
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = { NotesTopBar() },
        bottomBar = {
            NotesBottomBar(
                searchText = searchText,
                onSearchChange = { searchText = it },
                onCreateClick = { showDialog = true }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredPinned.isNotEmpty()) {
                item {
                    SectionHeader("置顶", true)
                }
                item {
                    NotesCard(filteredPinned) {
                        pinnedNotes.remove(it)
                    }
                }
            }

            if (filteredToday.isNotEmpty()) {
                item {
                    SectionHeader("今天", false)
                }
                item {
                    NotesCard(filteredToday) {
                        todayNotes.remove(it)
                    }
                }
            }

            if (filteredPinned.isEmpty() && filteredToday.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("没有找到备忘录", color = SecondaryText)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("新建备忘录") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("标题") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("内容") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank() ||
                            newContent.isNotBlank()
                        ) {
                            val time = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(Date())

                            todayNotes.add(
                                0,
                                Note(
                                    nextId++,
                                    newTitle.ifBlank { "无标题" },
                                    time,
                                    newContent
                                )
                            )
                        }

                        newTitle = ""
                        newContent = ""
                        showDialog = false
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun NotesTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundIconButton({
            Icon(Icons.Default.ArrowBack, "返回")
        })

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "备忘录",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Cloud,
                    "云存储",
                    tint = SecondaryText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    "储存空间已满",
                    color = SecondaryText,
                    fontSize = 12.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoundIconButton {
                Icon(Icons.Default.Share, "分享")
            }
            RoundIconButton {
                Icon(Icons.Default.MoreVert, "更多")
            }
        }
    }
}

@Composable
fun RoundIconButton(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun SectionHeader(title: String, showArrow: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (showArrow) {
            Icon(
                Icons.Default.ExpandMore,
                "展开",
                tint = AppleYellow,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
fun NotesCard(
    notes: List<Note>,
    onDelete: (Note) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            notes.forEachIndexed { index, note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            note.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${note.time}  ${note.content}",
                            fontSize = 14.sp,
                            color = SecondaryText,
                            maxLines = 2
                        )
                    }

                    IconButton(
                        onClick = { onDelete(note) }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "删除",
                            tint = Color.LightGray
                        )
                    }
                }

                if (index != notes.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFFE5E5EA),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun NotesBottomBar(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, "搜索")

            TextField(
                value = searchText,
                onValueChange = onSearchChange,
                placeholder = { Text("搜索") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Icon(Icons.Default.Mic, "语音")
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onCreateClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Edit,
                "新建备忘录"
            )
        }
    }
}
