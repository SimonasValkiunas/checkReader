package com.example.checkreader

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val galleryBtn = findViewById(R.id.open_gallery) as Button

        galleryBtn.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_GET_CONTENT
            );
            intent.type = "image/*";
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        }

        val cameraBtn = findViewById(R.id.add_photo) as Button

        cameraBtn.setOnClickListener {
            Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            ).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, 11)
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10){
            val picturePath = data!!.data;
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, picturePath)
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient()
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.textBlocks

                    val check_list = findViewById(R.id.check_list) as TextView
                    val total_sum = findViewById(R.id.total_sum) as TextView

                    var str = check_list.text.toString();
                    var sum_total = 0.0
                        if(total_sum.text.toString().isNotEmpty()) {
                            sum_total = total_sum.text.toString().replace(",",".").toDouble()
                        }

                    var is_next_line = false;
                    for (block in resultText){
                        for (line in block.lines){
                            if(line.text.contains("SUMA") || is_next_line) {
                                str += line.text + "\n"
                                if(is_next_line) sum_total += line.text.toString().replace("EUR", "").replace(",",".").toDouble();
                                is_next_line = !is_next_line;
                            }
                        }
                    }
                    check_list.text = str;
                    total_sum.text = sum_total.toString()
                }


        }else if (requestCode == 11){
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            val image = InputImage.fromBitmap(imageBitmap, 0)
            val recognizer = TextRecognition.getClient()
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.textBlocks

                    val check_list = findViewById(R.id.check_list) as TextView
                    val total_sum = findViewById(R.id.total_sum) as TextView

                    var str = check_list.text.toString();
                    var sum_total = 0.0
                    if(total_sum.text.toString().isNotEmpty()) {
                        sum_total = total_sum.text.toString().replace(",",".").toDouble()
                    }

                    var is_next_line = false;
                    for (block in resultText){
                        for (line in block.lines){
                            if(line.text.contains("SUMA") || is_next_line) {
                                str += line.text + "\n"
                                if(is_next_line) sum_total += line.text.toString().replace("EUR", "").replace(",",".").toDouble();
                                is_next_line = !is_next_line;
                            }
                        }
                    }
                    check_list.text = str;
                    total_sum.text = sum_total.toString()
                }

        }



    }





}
