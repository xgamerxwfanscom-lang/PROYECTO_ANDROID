package com.example.proyecto

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object PdfGenerator {

    fun generateServiceReport(
        context: Context,
        user: User?,
        hoursList: List<ServiceHour>,
        totalHours: Double
    ) {
        if (user == null) return

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        var yPos = 50f

        // Title
        paint.textSize = 20f
        paint.isFakeBoldText = true
        canvas.drawText("REPORTE DE SERVICIO SOCIAL", 50f, yPos, paint)
        yPos += 40f

        // User Info
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Nombre: ${user.fullName}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Matrícula: ${user.schoolId}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Escuela: ${user.schoolName}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Semestre: ${user.semester}", 50f, yPos, paint)
        yPos += 30f

        // Summary
        paint.isFakeBoldText = true
        canvas.drawText("PROGRESO TOTAL: $totalHours / ${user.requiredHours} horas", 50f, yPos, paint)
        yPos += 40f

        // Table Header
        paint.textSize = 10f
        canvas.drawText("Fecha", 50f, yPos, paint)
        canvas.drawText("Horas", 150f, yPos, paint)
        canvas.drawText("Descripción", 250f, yPos, paint)
        yPos += 10f
        canvas.drawLine(50f, yPos, 550f, yPos, paint)
        yPos += 20f

        // Table Content
        paint.isFakeBoldText = false
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        for (hour in hoursList) {
            if (yPos > 800) break 
            canvas.drawText(sdf.format(hour.date), 50f, yPos, paint)
            canvas.drawText(hour.hours.toString(), 150f, yPos, paint)
            val desc = if (hour.description.length > 40) hour.description.take(37) + "..." else hour.description
            canvas.drawText(desc, 250f, yPos, paint)
            yPos += 20f
        }

        pdfDocument.finishPage(page)

        val fileName = "Reporte_Servicio_${user.schoolId}_${System.currentTimeMillis()}.pdf"
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show()
                }
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(targetDir, fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(context, "PDF guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
