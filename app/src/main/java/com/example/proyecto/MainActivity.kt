package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.theme.PROYECTOTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: ServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PROYECTOTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

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
}

@Composable
fun LoginScreen(onLogin: (String, (Boolean) -> Unit) -> Unit, onNavigateToRegister: () -> Unit) {
    var schoolId by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Bienvenido de nuevo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Ingresa tu matrícula para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        OutlinedTextField(
            value = schoolId,
            onValueChange = { schoolId = it },
            label = { Text("Matrícula Escolar") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (schoolId.isBlank()) {
                    Toast.makeText(context, "Por favor ingresa tu matrícula", Toast.LENGTH_SHORT).show()
                } else {
                    onLogin(schoolId) { success ->
                        if (!success) {
                            Toast.makeText(context, "ID no encontrado. Regístrate primero.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToRegister) {
            Text("¿Eres nuevo? Crea una cuenta aquí", color = MaterialTheme.colorScheme.primary)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            "Crea tu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Completa tus datos de servicio social",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        StyledTextField(value = schoolId, onValueChange = { schoolId = it }, label = "Matrícula", icon = Icons.Default.AccountBox)
        StyledTextField(value = fullName, onValueChange = { fullName = it }, label = "Nombre Completo", icon = Icons.Default.Person)
        StyledTextField(value = schoolName, onValueChange = { schoolName = it }, label = "Escuela / Institución", icon = Icons.Default.Home)
        StyledTextField(value = semester, onValueChange = { semester = it }, label = "Semestre actual", icon = Icons.AutoMirrored.Filled.List)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StyledTextField(
                value = months, 
                onValueChange = { months = it }, 
                label = "Meses", 
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number
            )
            StyledTextField(
                value = hours, 
                onValueChange = { hours = it }, 
                label = "Total Horas", 
                icon = Icons.Default.Info,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
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
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ServiceViewModel, onShareText: (String) -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val hoursList by viewModel.hoursList.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    var hoursInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    
    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    val context = LocalContext.current

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Mi Servicio Social", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ) 
                },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header Card with Progress
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Hola, ${user?.fullName?.split(" ")?.firstOrNull() ?: ""}", 
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${user?.schoolName}", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "${user?.semester}° Sem", 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    val progress = (totalHours / (user?.requiredHours ?: 1.0)).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Progreso: ${(progress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "$totalHours / ${user?.requiredHours} hrs",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Input Section
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Registrar Actividad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hoursInput,
                            onValueChange = { hoursInput = it },
                            label = { Text("Horas") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                        
                        // Fecha Input
                        OutlinedTextField(
                            value = dateFormatter.format(Date(selectedDateMillis)),
                            onValueChange = { },
                            label = { Text("Fecha") },
                            modifier = Modifier.weight(1f),
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    
                    Button(
                        onClick = {
                            val h = hoursInput.toDoubleOrNull() ?: 0.0
                            if (h > 0 && descriptionInput.isNotBlank()) {
                                viewModel.addServiceHour(h, descriptionInput, selectedDateMillis)
                                hoursInput = ""
                                descriptionInput = ""
                                Toast.makeText(context, "¡Horas registradas!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Registro")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Historial de Actividades", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { onShareText(viewModel.generateReportText()) }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir")
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(hoursList) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        ListItem(
                            headlineContent = { Text("${item.hours} horas", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(item.description) },
                            overlineContent = { 
                                Text(
                                    SimpleDateFormat("EEEE, dd MMM", Locale("es", "MX")).format(item.date).replaceFirstChar { it.uppercase() },
                                    color = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
                
                if (hoursList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                            Text("Aún no tienes registros", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
        
        // Floating Action Button for PDF
        Box(modifier = Modifier.fillMaxSize()) {
            LargeFloatingActionButton(
                onClick = { PdfGenerator.generateServiceReport(context, user, hoursList, totalHours) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar PDF", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
