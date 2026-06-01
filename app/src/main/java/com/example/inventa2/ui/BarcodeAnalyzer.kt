package com.example.inventa2.ui

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// Esta clase recibe la imagen, busca el código y lo envía de regreso
class BarcodeAnalyzer(private val onBarcodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    // Inicializamos el escáner de inteligencia artificial de Google
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)


            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes.first()
                        barcode.rawValue?.let { valorEscaneado ->
                            onBarcodeDetected(valorEscaneado)
                        }
                    }
                }
                .addOnFailureListener {
                }
                .addOnCompleteListener {

                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}