package com.teknikcell.colorfilter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

enum class FilterColor {
    RED, GREEN, BLUE
}

class MainActivity : AppCompatActivity() {
    private lateinit var originalImageView: ImageView
    private lateinit var editedImageView: ImageView
    lateinit var rButton: Button
    lateinit var gButton: Button
    lateinit var bButton: Button

    lateinit var originalBitmap: Bitmap
    lateinit var editedBitmap: Bitmap
    lateinit var filterBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        originalImageView = findViewById(R.id.imageView)
        editedImageView = findViewById(R.id.editedImageView)
        rButton = findViewById(R.id.rButton)
        gButton = findViewById(R.id.gButton)
        bButton = findViewById(R.id.bButton)

        originalBitmap = (originalImageView.drawable as BitmapDrawable).bitmap

        editedImageView.setImageBitmap(compareBitmaps(originalBitmap, createFilter()))

        rButton.setOnClickListener {
            filterBitmap = createFilter()

            editedImageView.setImageBitmap(filterColor(originalBitmap, FilterColor.RED))
        }
        gButton.setOnClickListener {
            filterBitmap = createFilter()
            editedImageView.setImageBitmap(filterColor(originalBitmap, FilterColor.GREEN))
        }
        bButton.setOnClickListener {
            filterBitmap = createFilter()
            editedImageView.setImageBitmap(filterColor(originalBitmap, FilterColor.BLUE))
        }
    }

    private fun createFilter(): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(FILTER_SIZE, FILTER_SIZE, Bitmap.Config.ARGB_8888)
        for (x in 0 until FILTER_SIZE) {
            bitmap.setPixel(x, FILTER_MIDDLE_ROW, Color.BLACK)
        }

        return bitmap
    }

    private fun compareBitmaps(originalBitmap: Bitmap, filterBitmap: Bitmap): Bitmap {
        val filterImageHeight = filterBitmap.height
        val filterImageWidth = filterBitmap.width

        val originalImageHeight = originalBitmap.height
        val originalImageWidth = originalBitmap.width

        Log.e("originalImageHeight", originalImageHeight.toString())
        Log.e("originalImageWidth", originalImageWidth.toString())

        val newBitmap: Bitmap = Bitmap.createBitmap(originalImageWidth, originalImageHeight, Bitmap.Config.ARGB_8888)

        for (originalBitmapPixelX in 0 until originalImageWidth) {
            for (originalBitmapPixelY in 0 until originalImageHeight) {
                for (filterBitmapPixelX in 0 until filterImageWidth) {
                    for (filterBitmapPixelY in 0 until filterImageHeight) {
                        Log.e("->", "originalBitmapPixelX: " + originalBitmapPixelX.toString() + " | originalBitmapPixelY: " + originalBitmapPixelY)
                        try {
                            var originalPixel = originalBitmap.getPixel(originalBitmapPixelX, originalBitmapPixelY)
                            var filterPixel = filterBitmap.getPixel(filterBitmapPixelX, filterBitmapPixelY)

                            var originalRed: Int = Color.red(originalPixel)
                            var originalBlue: Int = Color.blue(originalPixel)
                            var originalGreen: Int = Color.green(originalPixel)
                            var originalAlpha: Int = Color.alpha(originalPixel)

                            var filterRed: Int = Color.red(filterPixel)
                            var filterBlue: Int = Color.blue(filterPixel)
                            var filterGreen: Int = Color.green(filterPixel)
                            Color.alpha(filterPixel)

                            var newPixel = Color.argb(
                                originalAlpha,
                                (originalRed + filterRed).coerceAtMost(MAX_CHANNEL_VALUE),
                                (originalGreen + filterGreen).coerceAtMost(MAX_CHANNEL_VALUE),
                                (originalBlue + filterBlue).coerceAtMost(MAX_CHANNEL_VALUE)
                            )

                            newBitmap.setPixel(originalBitmapPixelX, originalBitmapPixelY, newPixel)
                        } catch (err: Exception) {
                        }
                    }
                }
            }
        }
        return newBitmap
    }

    private fun filterColor(oldBitmap: Bitmap, filterColor: FilterColor): Bitmap {
        val newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val imageHeight = newBitmap.height
        val imageWidth = newBitmap.width

        for (pixelX in 0 until imageWidth) {
            for (pixelY in 0 until imageHeight) {
                val oldPixel: Int = oldBitmap.getPixel(pixelX, pixelY)

                val oldRed: Int = Color.red(oldPixel)
                val oldBlue: Int = Color.blue(oldPixel)
                val oldGreen: Int = Color.green(oldPixel)
                val oldAlpha: Int = Color.alpha(oldPixel)

                var newPixel: Int = 0

                when (filterColor) {
                    FilterColor.RED ->
                        newPixel = if (oldRed >= COLOR_INTENSITY && (oldGreen < COLOR_INTENSITY && oldBlue < COLOR_INTENSITY))
                            Color.argb(oldAlpha, oldRed, oldGreen, oldBlue)
                        else
                            Color.TRANSPARENT

                    FilterColor.GREEN -> {
                        newPixel = if (oldGreen >= COLOR_INTENSITY && (oldRed < COLOR_INTENSITY && oldBlue < COLOR_INTENSITY))
                            Color.argb(oldAlpha, oldRed, oldGreen, oldBlue)
                        else
                            Color.TRANSPARENT
                    }

                    FilterColor.BLUE -> {
                        newPixel = if (oldBlue >= COLOR_INTENSITY && (oldRed < COLOR_INTENSITY && oldGreen < COLOR_INTENSITY))
                            Color.argb(oldAlpha, oldRed, oldGreen, oldBlue)
                        else
                            Color.TRANSPARENT
                    }
                }
                newBitmap.setPixel(pixelX, pixelY, newPixel)
            }
        }
        return newBitmap
    }

    private companion object {
        const val FILTER_SIZE = 3
        const val FILTER_MIDDLE_ROW = FILTER_SIZE / 2
        const val MAX_CHANNEL_VALUE = 255
        const val COLOR_INTENSITY = 127
    }
}