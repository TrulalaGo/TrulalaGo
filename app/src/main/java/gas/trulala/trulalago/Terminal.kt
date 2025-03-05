package gas.trulala.trulalago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope

class Terminal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terminal)

        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )

        val perintahView = findViewById<EditText>(R.id.perintah)
        val hasil = findViewById<TextView>(R.id.hasil)
        val mulai = findViewById<TextView>(R.id.mulai)

        // Buat folder yang diperlukan
        Shell.sh("mkdir -p /data/data/gas.trulala.trulalago/files/usr/bin").exec()
        Shell.sh("chmod +x /data/data/gas.trulala.trulalago/files/usr/bin").exec()
        Shell.sh("mkdir -p /data/data/gas.trulala.trulalago/files/home").exec()

        mulai.setOnClickListener {
            val perintah = perintahView.text.toString().trim()
            if (perintah.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val info = Shell.sh(perintah).exec()
                val output = info.out.joinToString("\n")

                withContext(Dispatchers.Main) {
                    when (perintah) {
                        "exit" -> finish()
                        "clear" -> hasil.text = ""
                        else -> {
                            val pwd = Shell.sh("pwd").exec()
                            hasil.append(pwd.out.joinToString("\n"))
                            hasil.append("\n")
                            hasil.append("\n$perintah\n$output")
                        }
                    }
                    perintahView.text.clear()
                }
            }
        }
    }
}