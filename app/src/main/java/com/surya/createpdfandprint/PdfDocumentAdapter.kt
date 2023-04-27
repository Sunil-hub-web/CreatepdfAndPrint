package com.surya.createpdfandprint

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.printservice.PrintDocument
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PdfDocumentAdapter(mainActivity: MainActivity, path: String) : PrintDocumentAdapter() {

    internal var context : Context? = null
    internal var path = ""

    init {

        this.context = mainActivity
        this.path = path

    }

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        try {

            if (cancellationSignal != null) {
                if (cancellationSignal.isCanceled){

                    if (callback != null) {
                        callback.onLayoutCancelled()
                    }

                }else{

                    val builder = PrintDocumentInfo.Builder("file name")
                    builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                        .build()
                    if (callback != null) {
                        callback.onLayoutFinished(builder.build(),newAttributes != oldAttributes )
                    }
                }
            }
        }catch (e:Exception){


        }

    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {

        var inputs : InputStream? = null
        var outes : OutputStream? = null

        try {

            val file = File(path)

            inputs = FileInputStream(file)
            if (destination != null) {
                outes = FileOutputStream(destination.fileDescriptor)
            }

            if (!cancellationSignal!!.isCanceled){

                inputs.copyTo(outes!!)
                callback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

            }else{
                callback!!.onWriteCancelled()
            }

        }catch (e: Exception){
            callback!!.onWriteFailed(e.message)
            Log.d("sunilprintmessge", e.message!!)
        }finally {

            try {
                inputs!!.close()
                outes!!.close()
            }catch (e:Exception){
                Log.d("sunilprintmessge", e.message!!)
            }
        }
    }

}
