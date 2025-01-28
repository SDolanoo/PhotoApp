package com.example.photoapp.ui.ExcelPacker

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.photoapp.AppApplication
import com.example.photoapp.BuildConfig
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.Paragon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import javax.inject.Inject

@HiltViewModel
class ExportRoomViewModel @Inject constructor(
    private val repository: DatabaseRepository,
    private val context: Context,
): ViewModel() {

    val baseApplication = context.applicationContext

    fun exportParagon(
        workbook: XSSFWorkbook,
        listToExport: List<Paragon>
    ) {
        val sheet = workbook.createSheet("Paragony")

        val preferredParagonFields = arrayOf(
            "dataZakupu",
            "nazwaSklepu",
            "kwotaCalkowita"
        )

        val allParagonFields = Paragon::class.java.declaredFields

        val paragonFieldsToExport = arrayOfNulls<Field>(preferredParagonFields.size)

        for (i in preferredParagonFields.indices) {
            for (field in allParagonFields) {
                if (field.name == preferredParagonFields[i]) {
                    field.isAccessible = true // make private field accessible
                    paragonFieldsToExport[i] = field
                    break
                }
            }
        }
        // header row
        val headerRow = sheet.createRow(0)

        val headerStyle = createHeaderStyle(workbook)

        for (i in paragonFieldsToExport.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(paragonFieldsToExport[i]?.name)
            cell.cellStyle = headerStyle
        }

        //fetch data

        var rowNum = 1
        for (paragon in listToExport) {
            val row = sheet.createRow(rowNum++)
            for (i in paragonFieldsToExport.indices) {
                try {
                    // Access the field using reflection
                    val value = paragonFieldsToExport[i]?.get(paragon)
                    row.createCell(i).setCellValue(value.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    fun createHeaderStyle(workbook: XSSFWorkbook): XSSFCellStyle {

        // Create a new cell style for the header
        val headerStyle: XSSFCellStyle = workbook.createCellStyle()

        // Set background color
        headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.index
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        // Set font color and make it bold
        val font: XSSFFont = workbook.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.index
        headerStyle.setFont(font)

        // Set alignment
        headerStyle.alignment = HorizontalAlignment.CENTER
        headerStyle.verticalAlignment = VerticalAlignment.CENTER
        return headerStyle
    }

    suspend fun exportToExcel(listToExport: List<Paragon>) {
        try {
            val workbook = XSSFWorkbook()
            val folder = File(baseApplication.filesDir, "exported_files")
            //check if folder exists
            if (!folder.exists()) {
                folder.mkdirs()
            }
// If we want to add another sheet to the workbook, we simply call the function to //export that table and pass in the workbook
            exportParagon(workbook, listToExport = listToExport)
            val file = File(folder, "Paragony.xlsx")
            try {
                //write workbook to file
                withContext(Dispatchers.IO) {
                    FileOutputStream(file).use { outputStream ->
                        workbook.write(outputStream)
                    }
                    //Get file uri. In newer versions of android, enable buildconfig in app           // module
                    val fileUri = FileProvider.getUriForFile(
                        baseApplication.applicationContext,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file
                    )
                    //Open file
                    val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    //Share file
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooserIntent =
                        Intent.createChooser(
                            shareIntent,
                            "Open or Share File"
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    // Add the viewIntent as an extra option
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(viewIntent))
                    baseApplication.startActivity(chooserIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
