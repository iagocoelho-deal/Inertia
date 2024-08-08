package com.example.inertia

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inertia.databinding.ActivityMapLockerBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapLockerActivity : AppCompatActivity(), OnMapReadyCallback    {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapLockerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val textoRecebido = intent.getStringExtra("endereco") ?: ""


        binding = ActivityMapLockerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        adicionarTextViewNoMap(textoRecebido)


        val btnAlugar: Button = findViewById(R.id.btn_alugar)

        btnAlugar.setOnClickListener {

            val lockerSharedPersist = this.getSharedPreferences("locker_shared", MODE_PRIVATE)
            val locker_endereco = lockerSharedPersist.getString("locker_address","")


            val lockerData = RentLockerRequestDTO(
                locker_id = "d89a690d-e6cf-45b7-a34b-1d92c3c2f902",
                user_id = 1
            )

            val call = RetrofitFactory().retrofitService().rentLocker(lockerData)

            call.enqueue(object : Callback<RentLockerDTO> {

                override fun onResponse(call: Call<RentLockerDTO>, response: Response<RentLockerDTO>) {

                    response.let {
                        Log.i("TESTE-Response", Gson().toJson(response.body()))
                    }
                }

                override fun onFailure(call: Call<RentLockerDTO>?, t: Throwable?) {
                    t?.message?.let { it1 -> Log.e("Erro", it1) }
                }
            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val textoRecebido = intent.getStringExtra("endereco") ?: ""
        var LatLong: LatLng = LatLng(-23.5746685, -46.6232043)

        // FIAP Vila Olimpia
        val fiapVilaOlimpia = LatLng(-23.5955843, -46.6851937)

        // FIAP Paulista
        val fiapPaulista = LatLng(-23.5643721, -46.652857)

        // FIAP Vila Mariana
        val fiapVilaMariana = LatLng(-23.5746685, -46.6232043)


        when (textoRecebido) {
            "Rua Olimpíadas, 186" -> LatLong = fiapVilaOlimpia
            "Av Paulista, 1106" -> LatLong = fiapPaulista
            "Av Lins de Vasconcelos, 1264" -> LatLong = fiapVilaMariana
            else -> LatLong = fiapPaulista
        }


        saveOnSharedPreference("locker_address", textoRecebido)

        mMap.addMarker(MarkerOptions()
            .position(fiapVilaOlimpia)
            .title("Locker - FIAP Vila Olímpia")
            .snippet("Rua Olimpíadas, 186\nSão Paulo - SP\nCEP: 04551-000")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))

        mMap.addMarker(MarkerOptions()
            .position(fiapPaulista)
            .title("Locker - FIAP Paulista")
            .snippet("Av Paulista, 1106\nSão Paulo - SP\nCEP: 01311-000")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        mMap.addMarker(MarkerOptions()
            .position(fiapVilaMariana)
            .title("Locker - FIAP Vila Mariana")
            .snippet("Av Lins de Vasconcelos, 1264\nSão Paulo - SP\nCEP: 01538-001")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLong, 18F))

        mMap.setInfoWindowAdapter(object: GoogleMap.InfoWindowAdapter {

                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    val info = LinearLayout(applicationContext)
                    info.orientation = LinearLayout.VERTICAL


                    val title = TextView(applicationContext)
                    title.setTextColor(Color.BLACK)
                    title.gravity = Gravity.LEFT
                    title.setTypeface(null, Typeface.BOLD)
                    title.text = marker.title

                    val snippet = TextView(applicationContext)
                    snippet.setTextColor(Color.GRAY)
                    snippet.text = marker.snippet

                    info.addView(title)
                    info.addView(snippet)

                    return info
                }

        })

    }


    private fun adicionarTextViewNoMap(endereco: String) {
        val componenteLocker = layoutInflater.inflate(R.layout.activity_map_header, null)

        componenteLocker.findViewById<TextView>(R.id.text_map_address)?.text = endereco

        // Adicionar o componente ao layout principal
        val layoutPrincipal = findViewById<LinearLayout>(R.id.header_map)
        layoutPrincipal.addView(componenteLocker)
    }


    private fun saveOnSharedPreference(key: String, value: String) {
        val lockerSharedPref = this.getSharedPreferences("locker_shared", MODE_PRIVATE)
        val edit = lockerSharedPref.edit()

        edit.putString(key, value).apply()

        Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
    }

    fun handleAccessSuccessLocker(view: View) {
        val intent = Intent(this, SucessCreateLocker::class.java)
        startActivity(intent)
    }
}