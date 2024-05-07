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

        val lockerList = listOf(
            Locker("Rua Olimpíadas, 186"),
            Locker("Av Paulista, 1106"),
            Locker("Av Lins de Vasconcelos, 1264")
        )
        for (locker in lockerList) {
            adicionarLockerAoLayout(locker)
        }

    }


    private fun adicionarLockerAoLayout(locker: Locker) {
        // Inflar o componente do locker
        val componenteLocker = layoutInflater.inflate(R.layout.component_item_locker, null)

        // Configurar as informações do locker no componente
        componenteLocker.findViewById<TextView>(R.id.txv_endereco).text = locker.Endereco

        // Adicionar o componente ao layout principal
        val layoutPrincipal = findViewById<LinearLayout>(R.id.search_locker)
        layoutPrincipal.addView(componenteLocker)
    }

    fun acessarEnderecoNoMapa(view: View) {

        val textoDoTextView = view.findViewById<TextView>(R.id.txv_endereco).text

        val intent = Intent(this, MapLockerActivity::class.java)

        intent.putExtra("endereco", textoDoTextView)

        startActivity(intent)
    }
}