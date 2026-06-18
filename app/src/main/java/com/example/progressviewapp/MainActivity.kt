package com.example.progressviewapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.testTag as semanticsTestTag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

// --- Professional Theme Colors ---
val DarkNavyBg = Color(0xFF0F172A)
val CardNavy = Color(0xFF1E293B)
val SidebarNavy = Color(0xFF111827)
val BlueAccent = Color(0xFF3B82F6)
val GreenAccent = Color(0xFF10B981)
val PurpleAccent = Color(0xFF8B5CF6)
val AmberAccent = Color(0xFFF59E0B)
val RedAccent = Color(0xFFEF4444)
val TextTitle = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)

// --- Mock Data ---
val SUBJECTS = listOf("Mathematics", "Science", "English", "Social Studies", "Computer Science", "Tamil")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize().semantics { testTagsAsResourceId = true },
                color = MaterialTheme.colorScheme.background
            ) {
                ProgressViewApp()
            }
        }
    }
}

@Composable
fun ProgressViewApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = BlueAccent,
            background = DarkNavyBg,
            surface = CardNavy
        )
    ) {
        if (!isLoggedIn) {
            LoginScreen(onLogin = { isLoggedIn = true })
        } else {
            MainScaffold(navController, onLogout = { isLoggedIn = false })
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("parent@sunriseacademy.edu") }
    var password by remember { mutableStateOf("password123") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBg),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("login_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(BlueAccent, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("P", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Progress View", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextTitle)
                Text("Sunrise Academy • Parent Portal", fontSize = 12.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(32.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = RedAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RedAccent.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .testTag("login_error"),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth().testTag("email_field"),
                    isError = errorMessage.isNotEmpty(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth().testTag("password_field"),
                    isError = errorMessage.isNotEmpty(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (email == "parent@sunriseacademy.edu" && password == "password123") {
                            onLogin()
                        } else {
                            errorMessage = "Incorrect email or password"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign In", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(navController: NavHostController, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"
    
    val titles = mapOf(
        "dashboard" to "Dashboard",
        "profile" to "Student Profile",
        "attendance" to "Attendance",
        "tests" to "Daily Tests",
        "assignments" to "Assignments",
        "weekly" to "Weekly Reports",
        "subjects" to "Subject Performance",
        "yearly" to "Yearly Progress",
        "remarks" to "Teacher Remarks",
        "announcements" to "Announcements"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SidebarNavy,
                modifier = Modifier.width(280.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(20.dp))
                    SidebarHeader()
                    Spacer(Modifier.height(20.dp))
                    SidebarItems(navController, currentRoute) { scope.launch { drawerState.close() } }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                DynamicTopBar(
                    title = titles[currentRoute] ?: "Progress View",
                    isRoot = currentRoute == "dashboard",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onBackClick = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize().background(DarkNavyBg)) {
                NavHost(navController, startDestination = "dashboard") {
                    composable("dashboard") { DashboardScreen(navController) }
                    composable("profile") { StudentProfileScreen() }
                    composable("attendance") { AttendanceScreen() }
                    composable("tests") { TestsScreen() }
                    composable("assignments") { AssignmentsScreen() }
                    composable("weekly") { PlaceholderScreen("Weekly Reports") }
                    composable("subjects") { SubjectPerformanceScreen() }
                    composable("yearly") { PlaceholderScreen("Yearly Progress") }
                    composable("remarks") { TeacherRemarksScreen() }
                    composable("announcements") { AnnouncementsScreen() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicTopBar(
    title: String,
    isRoot: Boolean,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(title, color = TextTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Academic Year 2025–26", color = TextSecondary, fontSize = 11.sp)
            }
        },
        navigationIcon = {
            if (isRoot) {
                IconButton(onClick = onMenuClick, modifier = Modifier.testTag("menu_button")) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextTitle)
                }
            } else {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextTitle)
                }
            }
        },
        actions = {
            if (isRoot) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rejina", color = TextTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Parent", color = BlueAccent, fontSize = 11.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(34.dp).background(BlueAccent.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = BlueAccent, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.height(32.dp).testTag("signout_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Sign Out", fontSize = 11.sp, color = TextTitle)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SidebarNavy)
    )
}

@Composable
fun SidebarHeader() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BlueAccent, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Progress View", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Sunrise Academy", color = TextSecondary, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(BlueAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.testTag("sidebar_student_name")) {
                    Text("Sri Rakesh. R", color = TextTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Class 10-A • Roll 04", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SidebarItems(navController: NavHostController, currentRoute: String, onNavigate: () -> Unit) {
    val items = listOf(
        "Dashboard" to Icons.Default.Home to "dashboard",
        "Student Profile" to Icons.Default.Person to "profile",
        "Attendance" to Icons.Default.DateRange to "attendance",
        "Daily Tests" to Icons.Default.Edit to "tests",
        "Assignments" to Icons.Default.Assignment to "assignments",
        "Weekly Reports" to Icons.Default.BarChart to "weekly",
        "Subject Performance" to Icons.Default.MenuBook to "subjects",
        "Yearly Progress" to Icons.Default.TrendingUp to "yearly",
        "Teacher Remarks" to Icons.Default.Comment to "remarks",
        "Announcements" to Icons.Default.Notifications to "announcements"
    )

    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        items.forEach { (item, route) ->
            val label = item.first as String
            val icon = item.second as ImageVector
            
            NavigationDrawerItem(
                label = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(label, fontSize = 13.sp)
                        if (label == "Announcements") {
                            Spacer(Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .background(RedAccent, CircleShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                selected = currentRoute == route,
                onClick = { 
                    onNavigate()
                    if (currentRoute != route) navController.navigate(route) 
                },
                icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = BlueAccent.copy(alpha = 0.1f),
                    selectedIconColor = BlueAccent,
                    selectedTextColor = BlueAccent,
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.height(44.dp).testTag("sidebar_item_$route")
            )
        }
    }
}

// --- Dashboard Screen ---
@Composable
fun DashboardScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DashboardWelcomeBanner() }
        item { StatCardsGrid() }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(title = "Performance Trend", modifier = Modifier.weight(1f).height(220.dp)) {
                    SimpleLineChart(modifier = Modifier.fillMaxSize(), color = BlueAccent)
                }
                DashboardCard(title = "Attendance Breakdown", modifier = Modifier.weight(1f).height(220.dp)) {
                    SimplePieChart(modifier = Modifier.size(120.dp), percentage = 0.92f, color = GreenAccent)
                }
            }
        }
        item { Text("Quick Access", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        item { QuickAccessGrid(navController) }
    }
}

@Composable
fun DashboardWelcomeBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("welcome_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8))))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Good morning,", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("Rejina 👋", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Aryan's academic summary for today, Tuesday, 16 June", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StatCardsGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Attendance", "92%", "This academic year", GreenAccent, Icons.Default.DateRange, Modifier.weight(1f))
            StatCard("Latest Test", "87/100", "Mathematics", BlueAccent, Icons.Default.Edit, Modifier.weight(1f))
            StatCard("Avg Performance", "83%", "A Grade", PurpleAccent, Icons.Default.BarChart, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Pending Tasks", "3", "assignments due", AmberAccent, Icons.Default.Assignment, Modifier.weight(1f))
            StatCard("Announcements", "2", "unread notices", RedAccent, Icons.Default.Notifications, Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, sub: String, accent: Color, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier.border(width = 0.5.dp, color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, color = TextTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
fun DashboardCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

// --- Charts ---

@Composable
fun SimplePieChart(modifier: Modifier, percentage: Float, color: Color) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweep = 360f * percentage
            drawArc(
                color = color.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text("${(percentage * 100).toInt()}%", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun SimpleLineChart(modifier: Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val path = Path()
        val points = listOf(0.2f, 0.4f, 0.3f, 0.7f, 0.6f, 0.9f)
        val spacing = size.width / (points.size - 1)
        
        points.forEachIndexed { index, value ->
            val x = index * spacing
            val y = size.height - (value * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(path, color = color, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        
        // Add fill below
        val fillPath = path
        fillPath.lineTo(size.width, size.height)
        fillPath.lineTo(0f, size.height)
        fillPath.close()
        drawPath(fillPath, brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.3f), Color.Transparent)))
    }
}

@Composable
fun AttendanceBarChart(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val data = listOf(15f, 12f, 18f, 14f, 16f, 20f)
        val max = 20f
        val barWidth = 12.dp.toPx()
        val spacing = (size.width - (data.size * barWidth)) / (data.size + 1)
        
        data.forEachIndexed { index, value ->
            val x = spacing + index * (barWidth + spacing)
            val barHeight = (value / max) * size.height
            drawRoundRect(
                color = GreenAccent,
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
fun QuickAccessGrid(navController: NavHostController) {
    val options = listOf(
        "Attendance" to Icons.Default.DateRange to "attendance",
        "Daily Tests" to Icons.Default.Edit to "tests",
        "Reports" to Icons.Default.BarChart to "weekly",
        "Subjects" to Icons.Default.MenuBook to "subjects",
        "Tasks" to Icons.Default.Assignment to "assignments",
        "Notices" to Icons.Default.Notifications to "announcements"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { (item, route) ->
                    val label = item.first as String
                    val icon = item.second as ImageVector
                    QuickAccessButton(label, icon, Modifier.weight(1f)) { navController.navigate(route) }
                }
            }
        }
    }
}

@Composable
fun QuickAccessButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() }.testTag(
            when (label) {
                "Attendance" -> "quick_access_attendance"
                "Daily Tests" -> "quick_access_tests"
                "Tasks" -> "quick_access_assignments"
                else -> "quick_access_${label.lowercase()}"
            }
        ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, color = TextTitle, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// --- Specific Screens ---

@Composable
fun StudentProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(80.dp).background(BlueAccent, CircleShape), contentAlignment = Alignment.Center) {
                    Text("SR", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Text("SRI RAKESH", color = TextTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Roll No: 10A-04", color = TextSecondary, fontSize = 14.sp)
            }
        }
        ProfileInfoRow("Academic Year", "2025-26")
        ProfileInfoRow("Date of Birth", "15/03/2011")
        ProfileInfoRow("Parent Name", "Rajesh")
        ProfileInfoRow("Parent Email", "rajesh@email.com")
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextSecondary, fontSize = 14.sp)
            Text(value, color = TextTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AttendanceScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp).testTag("attendance_list"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Days", "15", "Active", BlueAccent, Icons.Default.DateRange, Modifier.weight(1f))
                StatCard("Present", "11", "73%", GreenAccent, Icons.Default.CheckCircle, Modifier.weight(1f))
                StatCard("Absent", "2", "13%", RedAccent, Icons.Default.Cancel, Modifier.weight(1f))
                StatCard("Late", "2", "13%", AmberAccent, Icons.Default.Schedule, Modifier.weight(1f))
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(title = "Monthly Attendance", modifier = Modifier.weight(1f).height(240.dp)) {
                    AttendanceBarChart(modifier = Modifier.fillMaxSize().padding(16.dp))
                }
                DashboardCard(title = "Attendance Distribution", modifier = Modifier.weight(1f).height(240.dp)) {
                    SimplePieChart(modifier = Modifier.size(140.dp), percentage = 0.73f, color = GreenAccent)
                }
            }
        }
        
        item { Text("Daily Attendance Log", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((1..15).toList()) { day ->
                    val color = when {
                        day % 6 == 0 -> RedAccent
                        day % 4 == 0 -> AmberAccent
                        else -> GreenAccent
                    }
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$day Jun", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(if(day % 6 == 0) "Absent" else if(day % 4 == 0) "Late" else "Present", color = color, fontSize = 8.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestsScreen() {
    val tests = listOf(
        "Mathematics" to 87, "Science" to 74, "English" to 92, "Social Studies" to 68, "Computer Science" to 95
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp).testTag("test_results_list"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Daily Test Results", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(tests) { test ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(test.first, color = TextTitle, fontWeight = FontWeight.Bold)
                        Text("${test.second}/100", color = BlueAccent, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { test.second / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = if (test.second > 80) GreenAccent else BlueAccent,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectPerformanceScreen() {
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            ScrollableRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SUBJECTS.forEach { subject ->
                    FilterChip(
                        selected = selectedSubject == subject,
                        onClick = { selectedSubject = subject },
                        label = { Text(subject) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueAccent,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.05f),
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }
        
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Tests Taken", "3", "This Term", AmberAccent, Icons.Default.Edit, Modifier.weight(1f))
                StatCard("Average", "86%", "Grade A", GreenAccent, Icons.Default.BarChart, Modifier.weight(1f))
                StatCard("Highest", "91%", "Excellent", BlueAccent, Icons.Default.TrendingUp, Modifier.weight(1f))
                StatCard("Lowest", "79%", "Good", RedAccent, Icons.Default.TrendingDown, Modifier.weight(1f))
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(title = "Score History", modifier = Modifier.weight(1f).height(240.dp)) {
                    SimpleLineChart(modifier = Modifier.fillMaxSize().padding(16.dp), color = BlueAccent)
                }
                DashboardCard(title = "All Subjects Comparison", modifier = Modifier.weight(1f).height(240.dp)) {
                    SubjectComparisonChart(modifier = Modifier.fillMaxSize().padding(16.dp))
                }
            }
        }
        
        item { Text("Test History - $selectedSubject", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        
        items(listOf(
            Triple("10/6/2025", "Daily", 87),
            Triple("28/5/2025", "Monthly", 79)
        )) { test ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(test.first, color = TextSecondary, fontSize = 12.sp)
                        Text(test.second, color = TextTitle, fontWeight = FontWeight.Bold)
                    }
                    Text("${test.third}/100", color = if(test.third > 80) GreenAccent else BlueAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubjectComparisonChart(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val data = listOf(85f, 75f, 90f, 65f, 95f, 80f)
        val barWidth = 12.dp.toPx()
        val spacing = (size.width - (data.size * barWidth)) / (data.size + 1)
        
        data.forEachIndexed { index, value ->
            val x = spacing + index * (barWidth + spacing)
            val barHeight = (value / 100f) * size.height
            drawRect(
                color = if (index == 0) BlueAccent else Color.White.copy(alpha = 0.1f),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun AssignmentsScreen() {
    val assignments = listOf(
        "Math: Quadratic Equations" to "Pending",
        "Science: Lab Report" to "Submitted",
        "History Project" to "Overdue"
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Assignments", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(assignments) { task ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(task.first, color = TextTitle, modifier = Modifier.weight(1f))
                    val color = when(task.second) {
                        "Submitted" -> GreenAccent
                        "Overdue" -> RedAccent
                        else -> AmberAccent
                    }
                    Text(task.second, color = color, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TeacherRemarksScreen() {
    val remarks = listOf(
        "Ms. Priya Rajan" to "Aryan has shown remarkable improvement.",
        "Mr. Venkat Rao" to "Concepts are strong, needs more practice."
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Teacher Remarks", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(remarks) { remark ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
                Column(Modifier.padding(16.dp)) {
                    Text(remark.first, color = BlueAccent, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("\"${remark.second}\"", color = TextSecondary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }
    }
}

@Composable
fun AnnouncementsScreen() {
    val news = listOf("Half-Yearly Exam Schedule", "PTM on June 20")
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("School Announcements", color = TextTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(news) { item ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardNavy)) {
                Column(Modifier.padding(16.dp)) {
                    Text(item, color = TextTitle, fontWeight = FontWeight.Bold)
                    Text("Click to view details", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize().background(DarkNavyBg), contentAlignment = Alignment.Center) {
        Text("$name Screen\n(Under Development)", color = TextTitle, textAlign = TextAlign.Center)
    }
}

@Composable
fun ScrollableRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}
