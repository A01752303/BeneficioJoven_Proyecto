package com.govAtizapan.beneficiojoven.view.home.generadorQR


import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

/**
 * 🔹 Generador de códigos QR para canjes o tokens.
 * Usa la librería ZXing para convertir texto (por ejemplo "CANJE-103")
 * en una imagen Bitmap que puede mostrarse en Jetpack Compose.
 */
object QRCodeGenerator {

    /**
     * Genera un QR Code a partir de un texto.
     *
     * @param contenido Texto o token que contendrá el QR (por ejemplo "CANJE-103").
     * @param size Tamaño del QR en píxeles (por defecto 512).
     * @return Bitmap con el QR generado, o null si hay error.
     */
    fun generarQR(contenido: String, size: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, size, size)

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }

            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}