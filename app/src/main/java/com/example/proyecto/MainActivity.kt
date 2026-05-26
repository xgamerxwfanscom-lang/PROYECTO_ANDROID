package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.theme.PROYECTOTheme
import com.example.proyecto.ui.theme.BlueDeep
import com.example.proyecto.ui.theme.OrangeVibrant
import com.example.proyecto.ui.theme.PaleBlue

class MainActivity : ComponentActivity() {
    private val viewModel: ServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PROYECTOTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (currentUser == null) {
                        AuthScreen(viewModel)
                    } else {
                        MainAppScreen(
                            viewModel = viewModel,
                            onShareText = { reportText -> shareReportText(reportText) }
                        )
                    }
                }
            }
        }
    }

    private fun shareReportText(report: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Reporte de Horas de Servicio Social")
            putExtra(Intent.EXTRA_TEXT, report)
        }
        startActivity(Intent.createChooser(intent, "Enviar reporte vía..."))
    }
}

@Composable
fun AuthScreen(viewModel: ServiceViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    
    if (isLogin) {
        LoginScreen(
            onLogin = { id, onResult -> viewModel.login(id, onResult) },
            onNavigateToRegister = { isLogin = false }
        )
    } else {
        RegisterScreen(
            onRegister = { user -> viewModel.registerUser(user) },
            onNavigateToLogin = { isLogin = true }
        )
    }
}

@Composable
fun LoginScreen(onLogin: (String, (Boolean) -> Unit) -> Unit, onNavigateToRegister: () -> Unit) {
    var schoolId by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineLarge, color = BlueDeep, fontWeight = FontWeight.Bold)
        Text("Inicia sesión con tu ID escolar", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = schoolId,
            onValueChange = { schoolId = it },
            label = { Text("ID Escolar (Matrícula)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                onLogin(schoolId) { success ->
                    if (!success) {
                        Toast.makeText(context, "ID no encontrado. Regístrate primero.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueDeep)
        ) {
            Text("Entrar")
        }
        
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate aquí", color = BlueDeep)
        }
    }
}

@Composable
fun RegisterScreen(onRegister: (User) -> Unit, onNavigateToLogin: () -> Unit) {
    var schoolId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Registro de Alumno", style = MaterialTheme.typography.headlineMedium, color = BlueDeep, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = schoolId, onValueChange = { schoolId = it }, label = { Text("Matrícula") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, label = { Text("Escuela / Carrera") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = semester, onValueChange = { semester = it }, label = { Text("Semestre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = months, 
            onValueChange = { months = it }, 
            label = { Text("Duración (Meses)") }, 
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = hours, 
            onValueChange = { hours = it }, 
            label = { Text("Total de horas requeridas") }, 
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (schoolId.isNotBlank() && fullName.isNotBlank()) {
                    onRegister(User(
                        schoolId = schoolId,
                        fullName = fullName,
                        schoolName = schoolName,
                        semester = semester,
                        totalMonths = months.toIntOrNull() ?: 0,
                        requiredHours = hours.toDoubleOrNull() ?: 0.0
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueDeep)
        ) {
            Text("Registrarse y Entrar")
        }
        
        TextButton(onClick = onNavigateToLogin) {
            Text("Ya tengo cuenta. Iniciar sesión", color = BlueDeep)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ServiceViewModel, onShareText: (String) -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val hoursList by viewModel.hoursList.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    var hoursInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MI SERVICIO SOCIAL", fontWeight = FontWeight.Bold, color = BlueDeep) },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = BlueDeep)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = PaleBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Tarjeta de Usuario con Gradiente y Barra de Progreso Blanca
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF8BBBD1), Color(0xFFBCE0EE))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Hola, ${user?.fullName}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = user?.schoolName?.uppercase() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF444444)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        
                        LinearProgressIndicator(
                            progress = { (totalHours / (user?.requiredHours ?: 1.0)).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val progressPercent = ((totalHours / (user?.requiredHours ?: 1.0)) * 100).toInt()
                            Text(
                                text = "$totalHours / ${user?.requiredHours} HRS - $progressPercent% Completo",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = "Ver Detalles",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Inputs rediseñados con BasicTextField para evitar el texto "aplastado"
            InputItem(
                value = hoursInput,
                onValueChange = { hoursInput = it },
                label = "Horas de hoy",
                placeholder = "Ejem: 4.5",
                icon = Icons.Default.Schedule,
                keyboardType = KeyboardType.Number
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            InputItem(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                label = "¿Qué hiciste hoy?",
                placeholder = "¿Qué hiciste hoy?",
                icon = Icons.Default.Edit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Registrar (Color Turquesa)
            Button(
                onClick = {
                    val h = hoursInput.toDoubleOrNull() ?: 0.0
                    if (h > 0 && descriptionInput.isNotBlank()) {
                        viewModel.addServiceHour(h, descriptionInput)
                        hoursInput = ""
                        descriptionInput = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF329999))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Horas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de PDF y Compartir
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(
                    text = "Descargar PDF",
                    icon = Icons.Default.KeyboardArrowDown,
                    color = Color(0xFF329999),
                    modifier = Modifier.weight(1f),
                    onClick = { PdfGenerator.generateServiceReport(context, user, hoursList, totalHours) }
                )
                SecondaryButton(
                    text = "Compartir Texto",
                    icon = Icons.Default.Share,
                    color = OrangeVibrant,
                    modifier = Modifier.weight(1f),
                    onClick = { onShareText(viewModel.generateReportText()) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Historial reciente:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta de Historial
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
                    hoursList.take(5).forEachIndexed { index, item ->
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "${sdf.format(item.date)} - ${item.hours} hrs - ${item.description}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF333333)
                            )
                        }
                        if (index < hoursList.take(5).size - 1) {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                    if (hoursList.isEmpty()) {
                        Text("No hay registros aún", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun InputItem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Color.Gray.copy(alpha = 0.5f), fontSize = 16.sp)
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = color,
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
