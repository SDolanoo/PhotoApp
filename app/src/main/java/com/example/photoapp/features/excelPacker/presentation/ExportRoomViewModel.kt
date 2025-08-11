package com.example.photoapp.features.excelPacker.presentation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolan.photoapp.BuildConfig
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import javax.inject.Inject

@HiltViewModel
class ExportRoomViewModel @Inject constructor(
    private val fakturaRepository: FakturaRepository,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    val context: Context,
): ViewModel() {

    val baseApplication = context.applicationContext

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

    suspend fun exportToExcel(whatToExport: String, listToExport: List<Any>) {
        val exportOptions = listOf("faktura")

        if (whatToExport in exportOptions) {
            Log.e("Dolan", "whatToExport VALUE IS INVALID IN EXPORT ROOM VIEW MODEL")
        }

        try {
            val workbook = XSSFWorkbook()
            val folder = File(baseApplication.filesDir, "exported_files")

            if (!folder.exists()) folder.mkdirs()

            when (whatToExport) {
                "faktura" -> {
                    val faktury = listToExport.filterIsInstance<Faktura>()
                    exportFaktura(workbook, faktury)
                }
            }

            val file = File(folder, "Exported.xlsx")
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream -> workbook.write(outputStream) }

                val fileUri = FileProvider.getUriForFile(
                    baseApplication.applicationContext,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(
                        fileUri,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooserIntent = Intent.createChooser(shareIntent, "Open or Share File")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(viewIntent)) }

                baseApplication.startActivity(chooserIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun exportFaktura(
        workbook: XSSFWorkbook,
        faktury: List<Faktura>
    ) = withContext(Dispatchers.IO) {
        val produktyFaktury: List<ProduktFakturaZProduktem> = fakturaRepository.getListProduktyFakturaZProduktemForListFaktura(faktury)

        val sheetFaktura = workbook.createSheet("Faktury")
        val sheetProdukt = workbook.createSheet("Produkty")

        val headerStyle = createHeaderStyle(workbook)

        // Nagłówki arkusza Faktura
        val fakturaHeaders = listOf(
            "ID Faktury", "Typ", "Nazwa Sprzedawcy", "NIP Sprzedawcy", "Nazwa Odbiorcy", "NIP Odbiorcy", "Numer", "Data Wystawienia", "Data Sprzedaży", "Miejsce Wystawienia",
            "Razem Netto", "Razem VAT", "Razem Brutto", "Do Zapłaty", "Waluta", "Forma Płatności"
        )

        // Wiersz nagłówków
        val fakturaHeaderRow = sheetFaktura.createRow(0)
        fakturaHeaders.forEachIndexed { index, header ->
            val cell = fakturaHeaderRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // Dane faktur
        faktury.forEachIndexed { rowIndex, faktura ->
            val odbiorca: Odbiorca = odbiorcaRepository.getById(faktura.odbiorcaId)!!
            val sprzedawca: Sprzedawca = sprzedawcaRepository.getById(faktura.sprzedawcaId)!!

            val row = sheetFaktura.createRow(rowIndex + 1)
            val cellValues = listOf(
                faktura.id.toString(),
                faktura.typFaktury,
                sprzedawca.nazwa,
                sprzedawca.nip,
                odbiorca.nazwa,
                odbiorca.nip,
                faktura.numerFaktury,
                faktura.dataWystawienia?.toString() ?: "",
                faktura.dataSprzedazy?.toString() ?: "",
                faktura.miejsceWystawienia,
                faktura.razemNetto,
                faktura.razemVAT,
                faktura.razemBrutto,
                faktura.doZaplaty,
                faktura.waluta,
                faktura.formaPlatnosci
            )
            cellValues.forEachIndexed { colIndex, value ->
                row.createCell(colIndex).setCellValue(value)
            }
        }

        // Arkusz Produkty - nagłówki
        val produktHeaders = listOf(
            "ID Faktury", "Nazwa", "Jednostka miary", "Cena Netto", "Stawka Vat", "Ilość",
            "Wartość Netto", "Wartość Brutto", "Rabat Wartość"
        )
        val produktHeaderRow = sheetProdukt.createRow(0)
        produktHeaders.forEachIndexed { index, header ->
            val cell = produktHeaderRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // Dane produktów
        var produktRowIndex = 1
        for (pfzp in produktyFaktury) { // pzpf = ProduktFakturaZProduktem
            val row = sheetProdukt.createRow(produktRowIndex++)

            val cellValues = listOf(
                pfzp.produktFaktura.fakturaId.toString(),
                pfzp.produkt.nazwaProduktu,
                pfzp.produkt.jednostkaMiary,
                pfzp.produkt.cenaNetto,
                pfzp.produkt.stawkaVat,
                pfzp.produktFaktura.ilosc,
                pfzp.produktFaktura.wartoscNetto,
                pfzp.produktFaktura.wartoscBrutto,
                pfzp.produktFaktura.rabat
            )
            cellValues.forEachIndexed { colIndex, value ->
                row.createCell(colIndex).setCellValue(value)
            }
        }
    }


}