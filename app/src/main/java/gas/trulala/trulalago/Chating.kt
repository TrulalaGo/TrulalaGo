package gas.trulala.trulalago

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.content.Intent

class Chating : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chating)

        val editText = findViewById<EditText>(R.id.pesan)
        val hasilKirim = findViewById<TextView>(R.id.hasilKirim)
        val button = findViewById<Button>(R.id.kirim)

        button.setOnClickListener {
            val pesan = editText.text.toString()
            hasilKirim.text = pesan // Menampilkan pesan yang dikirim
            editText.text.clear() // Mengosongkan EditText setelah mengirim
        }
    }
}