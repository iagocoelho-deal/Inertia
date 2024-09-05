package com.example.inertia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inertia.DTO.GetLockersResponseDTO
import com.example.inertia.api.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class SearchLocker : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_locker)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_locker)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val call = RetrofitFactory().retrofitService().getLockers()

        call.enqueue(object : Callback<List<GetLockersResponseDTO>> {
            override fun onResponse(call: Call<List<GetLockersResponseDTO>>, response: Response<List<GetLockersResponseDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let { lockerList ->
                        for (locker in lockerList) {
                            if (locker.free) {
                                return adicionarLockerAoLayout(Locker(locker.address, locker.id))
                            }
                        }
                    } ?: run {
                        Log.e("TESTE-GET", "O corpo da resposta é nulo")
                    }
                } else {
                    Log.e("TESTE-GET", "Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GetLockersResponseDTO>>, t: Throwable) {
                Log.e("Erro", t.message ?: "Erro desconhecido")
            }
        })
    }
    private fun adicionarLockerAoLayout(locker: Locker) {
        val componenteLocker = layoutInflater.inflate(R.layout.component_item_locker, null)

        val enderecoTextView = componenteLocker.findViewById<TextView>(R.id.txv_endereco)
        enderecoTextView.text = locker.Endereco

        componenteLocker.setTag(R.id.txv_endereco, locker.uuid)

        componenteLocker.setOnClickListener { view ->
            val lockerId = view.getTag(R.id.txv_endereco) as UUID

            acessarEnderecoNoMapa(view, lockerId)
        }

        val layoutPrincipal = findViewById<LinearLayout>(R.id.search_locker)
        layoutPrincipal.addView(componenteLocker)
    }

    private fun acessarEnderecoNoMapa(view: View, lockerId: UUID) {
        val textoDoTextView = view.findViewById<TextView>(R.id.txv_endereco).text

        val intent = Intent(this, MapLockerActivity::class.java)

        intent.putExtra("endereco", textoDoTextView)
        intent.putExtra("lockerId", lockerId.toString())

        startActivity(intent)
    }
}