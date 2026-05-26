package com.example.proyecto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).serviceDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val hoursList: StateFlow<List<ServiceHour>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> dao.getHoursByUserId(user.schoolId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHours: StateFlow<Double> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> dao.getTotalHours(user.schoolId) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun registerUser(user: User) {
        viewModelScope.launch {
            dao.insertUser(user)
            _currentUser.value = user
        }
    }

    fun login(schoolId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = dao.getUserById(schoolId)
            if (user != null) {
                _currentUser.value = user
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addServiceHour(hours: Double, description: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val newEntry = ServiceHour(
                userId = user.schoolId,
                hours = hours,
                description = description
            )
            dao.insertHour(newEntry)
        }
    }

    fun generateReportText(): String {
        val user = _currentUser.value
        val total = totalHours.value
        val details = hoursList.value.joinToString("\n") {
            "Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it.date)} - Horas: ${it.hours} - Desc: ${it.description}"
        }
        
        return """
            REPORTE DE SERVICIO SOCIAL
            Alumno: ${user?.fullName ?: "N/A"}
            Matrícula: ${user?.schoolId ?: "N/A"}
            Escuela: ${user?.schoolName ?: "N/A"}
            Semestre: ${user?.semester ?: "N/A"}
            Progreso: $total / ${user?.requiredHours ?: 0.0} horas
            
            DETALLES DE ACTIVIDADES:
            $details
        """.trimIndent()
    }
}
