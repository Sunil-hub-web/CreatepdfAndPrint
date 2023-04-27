package com.surya.createpdfandprint

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.surya.createpdfandprint.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    val file_name : String = "test_pdf.pdf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object:PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.btnCPDF.setOnClickListener{

                        createpdfFile(Comman.getAppPath(this@MainActivity)+file_name)
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            })
            .check()

    }

    fun createpdfFile(path : String){

        if (File(path).exists())
            File(path).delete()

        try {

            val document = Document()

            PdfWriter.getInstance(document,FileOutputStream(path))
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("SunilKumar")
            document.addCreator("Kumar Dash")

            val colorAccent = BaseColor(0,153,204,255)
            val headingFontSize = 20.0f
            val valueFontSize = 20.0f
            val fontName = BaseFont.createFont("assets/font/muli_bold.ttf","UTF-8",BaseFont.EMBEDDED)

            val titleStyle = Font(fontName,36.0f,Font.NORMAL, BaseColor.BLACK)
            addNewItem(document,"Order Details",Element.ALIGN_CENTER,titleStyle)

            val headingStyle = Font(fontName,headingFontSize,Font.NORMAL, colorAccent)
            addNewItem(document,"Order No",Element.ALIGN_LEFT,headingStyle)

            val valueStyle = Font(fontName,valueFontSize,Font.NORMAL, BaseColor.BLACK)
            addNewItem(document,"#123123",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)

            addNewItem(document,"Order date :",Element.ALIGN_LEFT,headingStyle)
            addNewItem(document,"03/08/2019",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)

            addNewItem(document,"Account No",Element.ALIGN_LEFT,headingStyle)
            addNewItem(document,"Eddy Lee",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)
            addLineSpace(document)

            addNewItem(document,"Product Details",Element.ALIGN_LEFT,titleStyle)

            addLineSeperator(document)

            addNewItemWithleftAndRight(document,"Pizza 25","(0.0%)",titleStyle,valueStyle)
            addNewItemWithleftAndRight(document,"12.0*10000","12000.0",titleStyle,valueStyle)

            addLineSeperator(document)

            addNewItemWithleftAndRight(document,"Pizza 26","(0.0%)",titleStyle,valueStyle)
            addNewItemWithleftAndRight(document,"12.0*10000","12000.0",titleStyle,valueStyle)

            addLineSeperator(document)

            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithleftAndRight(document,"Total","24000.0",titleStyle,valueStyle)

            Toast.makeText(this@MainActivity,"PDF File Created",Toast.LENGTH_SHORT).show()

            document.close()

            printPdfDocument()

        }catch (e:Exception){

            Log.d("sunilprint",""+e.message)
        }

    }

    private fun printPdfDocument() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager

        try {

            val printAdapter = PdfDocumentAdapter(this@MainActivity,Comman.getAppPath(this@MainActivity)+file_name)
            printManager.print("Document",printAdapter,PrintAttributes.Builder().build())

        }catch (e : Exception){

        }
    }

    private fun addNewItemWithleftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font) {

        val chunkTextLeft = Chunk(textLeft,leftStyle)
        val chunkTextRight = Chunk(textRight,rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)


    }

    private fun addLineSeperator(document: Document) {

        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0,0,0,68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    private fun addLineSpace(document: Document) {

        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    fun addNewItem(document: Document, text : String, align : Int, style : Font){

        val chunk = Chunk(text,style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }
}