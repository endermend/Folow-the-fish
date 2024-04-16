package com.example.fishhunt


import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject


class FishHuntActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var description: Button
    private lateinit var photo: ImageView
    private val urlStart = "https://1c91-2a03-d000-7005-f007-4e7b-2d90-ed33-1e8f.ngrok.io"
    private var arrayId = IntArray(0)
    private var randomId = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_hunt)
        name = findViewById(R.id.name)
        description = findViewById(R.id.description)
        photo = findViewById(R.id.photo)

        val arguments = getIntent()
        val temp : IntArray?
        temp = arguments?.getIntArrayExtra("arrayId")
        if (temp!=null){
            arrayId = temp
        }


        randomId = arrayId.random()
        val url_for_get_info = "$urlStart/fishes_list/${randomId}/"
        make_get_request(url_for_get_info)

//        go_photo.setOnClickListener {
//            Log.d("-", "-")
//        }

    }

    fun back_to_menu(view: View) {
        val randomIntent = Intent(this, MainActivity2::class.java)
        startActivity(randomIntent)
        finish()
    }

    fun go_photo(view: View) {
        val randomIntent = Intent(this, CameraActivity::class.java)
        randomIntent.putExtra("randomId", randomId)
        randomIntent.putExtra("arrayId", arrayId)
        randomIntent.putExtra("urlStart", urlStart)
        startActivity(randomIntent)
        finish()

    }

    fun make_get_request(url : String) {
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val json = JSONObject(response)
//                now_id_fish = json.getString("id")
                val name_resp = "Ваша цель:\n" + reverse(json.getString("name"))
                val description_resp = reverse(json.getString("description"))
                name.text = name_resp
                description.text = description_resp

            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "Network_error", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
        make_picasso(photo)
    }

    fun reverse(start_str : String):String{
        var result = ""
        var temp : Char
        val byte_string = start_str.toByteArray()
        var i = 0
        val end_str = byte_string.size
        while (i < end_str){
            if (byte_string[i].toInt()==61*-1){
                temp = getAlphaChar(byte_string[i+1].toInt()*-1, byte_string[i+3].toInt()*-1)
                i += 4
                result += temp
            }
            else if(byte_string[i].toInt()==45){
                result += '-'
                i+=1
            }
            else if((byte_string[i].toInt()>=48) && (byte_string[i].toInt()<=57)){
                val temp_str = "0123456789---"
                result += temp_str[byte_string[i].toInt()-48]
                i+=1
            }
            else if(byte_string[i].toInt()==13 && byte_string[i+1].toInt()==10){
                i += 2
                result += "\n"
            }
            else{
                when(byte_string[i].toInt()){
                    (32) -> result += " "
                    (44) -> result += ","
                    (46) -> result += "."
                }
                i += 1
            }
        }
        return result
    }

    fun getAlphaChar(a1:Int, a2:Int):Char{
        val low_s = "абвгдежзийклмнопрстуфхцчшщъыьэюя"
        val up_s = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
        val s = " "
        try {
            if (a1 == 112) {
                if (a2 > 80) {
                    val t = (a2 - 112) * -1
                    return up_s[t]
                }
                val t = (a2 - 80) * -1
                return low_s[t]

            }
            if (a1 == 111 && (113 <= a2) && (128 >= a2)) {
                val t = (a2 - 128) * -1 + 16
                return low_s[t]
            }
        }
        finally {}
        return ' '
    }





    fun make_picasso(item : ImageView) {
        var temp = R.drawable.fish_1
        when(randomId){
            (1) -> temp=R.drawable.fish_1
            (2) -> temp=R.drawable.fish_2
            (3) -> temp=R.drawable.fish_3
            (4) -> temp=R.drawable.fish_4
        }

        Picasso.with(this)
            .load(temp)
            .centerCrop()
            .fit()
            .into(item);

    }


}
