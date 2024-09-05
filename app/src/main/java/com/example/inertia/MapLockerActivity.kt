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
import com.example.inertia.DTO.RentLockerDTO
import com.example.inertia.DTO.RentLockerRequestDTO
import com.example.inertia.api.RetrofitFactory
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
    private var lockerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val lockerEndereco = intent.getStringExtra("endereco") ?: ""
        lockerId = intent.getStringExtra("lockerId")

        // Verifique se os dados foram recuperados corretamente
        Log.d("MapLockerActivity", "Endereço: $lockerEndereco")
        Log.d("MapLockerActivity", "Locker ID: $lockerId")


        binding = ActivityMapLockerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        adicionarTextViewNoMap(lockerEndereco)


        val btnAlugar: Button = findViewById(R.id.btn_alugar)

        btnAlugar.setOnClickListener {
            val lockerData = lockerId?.let {
                RentLockerRequestDTO(
                    lockerId = lockerId!!,
                    userId = 1,
                    rentStartDate = "2024-09-05T10:00:00Z",
                    rentFinishDate = "2024-09-10T10:00:00Z"
                )
            }

            lockerData?.let { data ->
                val call = RetrofitFactory().retrofitService().rentLocker(data)

                Log.d("MapLockerActivity", "Objeto POST: $lockerData")

                call.enqueue(object : Callback<RentLockerDTO> {
                    override fun onResponse(call: Call<RentLockerDTO>, response: Response<RentLockerDTO>) {
                        response.let {
                            Log.i("TESTE-POST", Gson().toJson(response.body()))

                            Toast.makeText(this@MapLockerActivity,
                                "O locker foi alugado com sucesso!", Toast.LENGTH_LONG).show()

                            handleAccessSuccessLocker()
                        }
                    }

                    override fun onFailure(call: Call<RentLockerDTO>, t: Throwable) {
//                        t.message?.let { it1 -> Log.e("Erro", it1) }
                        Toast.makeText(this@MapLockerActivity, "Ocorreu um erro ao alugar este locker", Toast.LENGTH_LONG).show()
                    }
                })
            } ?: run {
                Log.e("MapLockerActivity", "Locker ID não disponível")
            }
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

    fun handleAccessSuccessLocker() {
        val intent = Intent(this, SucessCreateLocker::class.java)
        startActivity(intent)
    }
}