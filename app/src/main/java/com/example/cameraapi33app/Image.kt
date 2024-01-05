package com.example.cameraapi33app

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.annotation.CheckResult
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


const val TAG = "Image.kt"

/**
 * crop bitmap based on given Rect
 */
fun Bitmap.crop(crop: Rect): Bitmap {
    require(crop.left < crop.right) {
        "left < right (${crop.left} & ${crop.right})"
    }
    require(crop.top < crop.bottom){
        "top < bottom (${crop.top} & ${crop.bottom})"
    }
    //require(crop.left < crop.right && crop.top < crop.bottom) { "Cannot use negative crop" }
    require(crop.left >= 0){
        "left >= 0 : ${crop.left}"
    }
    require(crop.top >= 0){
        "top >= 0 : ${crop.top}"
    }
    require(crop.bottom <= this.height){
        "bottom >= this.height : ${crop.bottom} >= ${this.height}"
    }
    require(crop.right <= this.width){
        "right >= this.width : ${crop.right} >= ${this.width}"
    }

    /*require(crop.left >= 0 && crop.top >= 0 && crop.bottom <= this.height && crop.right <= this.width) {
        "Crop is outside the bounds of the image"
    }*/
    return Bitmap.createBitmap(this, crop.left, crop.top, crop.width(), crop.height())
}

/**
 * Get the size of a bitmap.
 */
fun Bitmap.size(): Size = Size(this.width, this.height)

/**
 * Calculate the crop from the [fullImage] for the credit card based on the [cardFinder] within the [previewImage].
 *
 * Note: This algorithm makes some assumptions:
 * 1. the previewImage and the fullImage are centered relative to each other.
 * 2. the fullImage circumscribes the previewImage. I.E. they share at least one field of view, and the previewImage's
 *    fields of view are smaller than or the same size as the fullImage's
 * 3. the fullImage and the previewImage have the same orientation
 */
fun cropImage(fullImage: Bitmap, previewSize: Size, cardFinder: Rect): Bitmap {
    require(
        cardFinder.left >= 0 &&
                cardFinder.right <= previewSize.width &&
                cardFinder.top >= 0 &&
                cardFinder.bottom <= previewSize.height
    ) { "Card finder is outside preview image bounds" }



    // Position the scaledCardFinder on the fullImage
    val cropRect = Rect(
        0,
        0,
        1800,
        1800
    )

    return fullImage.crop(cropRect)
}


fun storeImage(image: Bitmap, photoFile: File): Uri {
    try {
        val fos = FileOutputStream(photoFile)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
    } catch (e: FileNotFoundException) {
        Log.d(TAG, "File not found: " + e.message)
    } catch (e: IOException) {
        Log.d(TAG, "Error accessing file: " + e.message)
    }
    return Uri.fromFile(photoFile)
}