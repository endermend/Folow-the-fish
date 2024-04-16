package com.example.fishhunt

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class CameraActivity : AppCompatActivity() {
    private const val REQUEST_TAKE_PHOTO = 1
    var urlStart : String = ""
    private var arrayId = IntArray(0)
    private var randomId : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        val arguments = getIntent()

        val temp1 : IntArray?
        temp1 = arguments?.getIntArrayExtra("arrayId")
        if (temp1!=null){
            arrayId = temp1
        }


        val temp2 : Int?
        temp2 = arguments?.getIntExtra("randomId", 1)
        if (temp2!=null){
            randomId = temp2
        }

        val temp3 : String?
        temp3 = arguments?.getStringExtra("urlStart")
        if (temp3!=null){
            urlStart = temp3
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            val randomIntent = Intent(this, MainActivity2::class.java)
            Toast.makeText(applicationContext, "CameraActivity_error", Toast.LENGTH_SHORT).show()
            startActivity(randomIntent)
            finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            val thumbnailBitmap = data?.extras?.get("data") as Bitmap

            val url_for_get_info = "$urlStart/validate_fish/"

            val bitmap = thumbnailBitmap
            val stream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val encodedString: String = Base64.getEncoder().encodeToString(image)
            make_post_request(url_for_get_info, encodedString)
            Log.d("-", "----")
//            imageView.setImageBitmap(thumbnailBitmap)
        }
        else{
            val randomIntent = Intent(this, FishHuntActivity::class.java)
            Toast.makeText(applicationContext, "CameraCancel_error", Toast.LENGTH_SHORT).show()
            startActivity(randomIntent)
            finish()
        }
    }
    private fun make_post_request(url : String, image : String) {
        val queue = Volley.newRequestQueue(this);
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                var temp = JSONObject(response)
                Log.d("-", response.toString())
                if(temp.getBoolean("result")){
                    access()
                }
                else{
                    fail()
                }
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "Network_error", Toast.LENGTH_SHORT).show()
                not_work()

            })
        {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["fish_id"] = randomId.toString()
                params["image"] = image
                return params
            }

        }

        queue.add(stringRequest)
    }

    private fun access(){
        val randomIntent = Intent(this, ResultOkActivity::class.java)
        randomIntent.putExtra("randomId", randomId)
        randomIntent.putExtra("arrayId", arrayId)
        startActivity(randomIntent)
        finish()
    }
    private fun fail(){
        val randomIntent = Intent(this, ResultFailActivity::class.java)
        randomIntent.putExtra("randomId", randomId)
        randomIntent.putExtra("arrayId", arrayId)
        startActivity(randomIntent)
        finish()
    }

    private fun not_work(){
        val randomIntent = Intent(this, FishHuntActivity::class.java)
        randomIntent.putExtra("arrayId", arrayId)
        startActivity(randomIntent)
        finish()
    }


}