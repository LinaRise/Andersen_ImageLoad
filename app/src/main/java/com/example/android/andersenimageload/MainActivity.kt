package com.example.android.andersenimageload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns.WEB_URL
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.android.andersenimageload.utilits.toast
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var picassoBtn: Button
    private lateinit var glideBtn: Button
    private lateinit var androiodBtn: Button
    private lateinit var linkEdt: EditText
    private lateinit var imageView: ImageView
    private var link = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        picassoBtn = findViewById(R.id.btn_picasso)
        glideBtn = findViewById(R.id.btn_glide)
        androiodBtn = findViewById(R.id.btn_android)
        linkEdt = findViewById(R.id.edt_link)
        imageView = findViewById(R.id.imageView)

        picassoBtn.setOnClickListener {
            link = linkEdt.text.toString().trim()
            if (link.isEmpty()) {
                toast(getString(R.string.link_to_picture_ask))
            } else {
                if (WEB_URL.matcher(link).matches()) {
                    Picasso.get()
                        .load(link)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_error)
                        .centerCrop()
                        .resize(600, 600)
                        .into(imageView, object : Callback {
                            override fun onSuccess() {
                            }

                            override fun onError(e: Exception?) {
                                toast("Error: $e")
                            }
                        })
                } else {
                    toast(getString(R.string.not_link))
                }
            }
        }

        glideBtn.setOnClickListener {
            link = linkEdt.text.toString().trim()
            if (link.isEmpty()
            ) {
                toast(getString(R.string.link_to_picture_ask))
            } else {

                if (WEB_URL.matcher(link).matches()) {
                    Glide
                        .with(this)
                        .load(link)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache( true )
                        .apply(
                            RequestOptions()
                                .error(R.drawable.ic_error)
                                .centerCrop()
                                .placeholder(R.drawable.ic_image)

                        )
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                toast("Error: $e")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(imageView)
                } else {
                    toast(getString(R.string.not_link))
                }
            }
        }

        androiodBtn.setOnClickListener {
            link = linkEdt.text.toString().trim()
            if (link.isEmpty()
            ) {
                toast(getString(R.string.link_to_picture_ask))
            } else {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val bitmap = downloadBitmapFromUrl(link)
                    handler.post {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                        } else {
                            imageView.setImageResource(R.drawable.ic_image)
                        }
                    }
                }
            }
        }
    }

    private fun downloadBitmapFromUrl(link: String): Bitmap? {
        return try {
            val connection = URL(link).openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: Exception) {
            this.runOnUiThread {
                toast("Error: $e")
            }
            null
        }
    }
}