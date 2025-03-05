package gas.trulala.trulalago

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import android.content.Intent
import androidx.core.content.FileProvider

class Berkas : AppCompatActivity() {
    private lateinit var currentDir: File
    private lateinit var grid: GridLayout
    private lateinit var jalur: TextView
    private lateinit var lama: LinearLayout
    private lateinit var salin: ImageView
    private lateinit var hapus: ImageView
    private lateinit var potong: ImageView
    private var selectedFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.berkas)

        currentDir = Environment.getExternalStorageDirectory()
        grid = findViewById(R.id.grid)
        jalur = findViewById(R.id.jalur)
        lama = findViewById(R.id.lama)
        salin = findViewById(R.id.salin)
        hapus = findViewById(R.id.hapus)
        potong = findViewById(R.id.potong)

        val fileBaru = findViewById<ImageView>(R.id.fileBaru)
        val folderBaru = findViewById<ImageView>(R.id.folderBaru)
        val sebelumnya = findViewById<ImageView>(R.id.sebelumnya)

        refreshGrid()
        updateJalur()

        sebelumnya.setOnClickListener { pindahKeParent() }
        fileBaru.setOnClickListener { showCreateDialog(true) }
        folderBaru.setOnClickListener { showCreateDialog(false) }
        grid.setOnClickListener { lama.visibility = View.GONE }
    }

    private fun refreshGrid() {
        grid.removeAllViews()
        if (!currentDir.exists() || !currentDir.isDirectory) {
            Toast.makeText(this, "Direktori tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val files = currentDir.listFiles()?.sortedBy { !it.isDirectory } ?: run {
            Toast.makeText(this, "Tidak dapat mengakses direktori", Toast.LENGTH_SHORT).show()
            return
        }

        for (file in files) {
            val tampilan = LayoutInflater.from(this).inflate(R.layout.memo_vertical, grid, false)
            val gambar = tampilan.findViewById<ImageView>(R.id.gambar)
            val nama = tampilan.findViewById<TextView>(R.id.nama)

            nama.text = file.name
            gambar.setImageResource(if (file.isDirectory) R.drawable.folder else R.drawable.file)

            tampilan.setOnClickListener {
                if (file.isDirectory) {
                    currentDir = file
                    refreshGrid()
                    updateJalur()
                } else {
                    openFile(file)
                }
            }

            tampilan.setOnLongClickListener {
                lama.visibility = View.VISIBLE
                setupFileActions(file)
                true
            }

            grid.addView(tampilan)
        }
    }

    private fun setupFileActions(file: File) {
        selectedFile = file
        salin.setOnClickListener { copyFile(file) }
        hapus.setOnClickListener { 
            if (file.delete()) {
                refreshGrid()
                Toast.makeText(this, "Berhasil menghapus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
            }
        }
        potong.setOnClickListener { cutFile(file) }
    }

    private fun showCreateDialog(isFile: Boolean) {
        val tulisNama = LayoutInflater.from(this).inflate(R.layout.item_ketik, null)
        val tulis = tulisNama.findViewById<EditText>(R.id.tulis)
        val batal = tulisNama.findViewById<Button>(R.id.batal)
        val mulai = tulisNama.findViewById<Button>(R.id.mulai)

        val dialog = AlertDialog.Builder(this)
            .setView(tulisNama)
            .create()

        batal.setOnClickListener { dialog.dismiss() }
        mulai.setOnClickListener {
            val nama = tulis.text.toString().trim()
            if (nama.isNotEmpty()) {
                val newFile = File(currentDir, nama)
                if (newFile.exists()) {
                    Toast.makeText(this, "Nama sudah ada", Toast.LENGTH_SHORT).show()
                } else {
                    val success = if (isFile) newFile.createNewFile() else newFile.mkdir()
                    if (success) {
                        refreshGrid()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Gagal membuat ${if (isFile) "file" else "folder"}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun pindahKeParent() {
        currentDir.parentFile?.let {
            currentDir = it
            refreshGrid()
            updateJalur()
        }
    }

    private fun updateJalur() {
        jalur.text = currentDir.absolutePath
    }

    private fun openFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val mimeType = when (file.extension) {
            "txt" -> "text/plain"
            "pdf" -> "application/pdf"
            "jpg", "jpeg", "png" -> "image/*"
            else -> "*/*"
        }
        intent.setDataAndType(uri, mimeType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun copyFile(file: File) {
        val destFile = File(currentDir, "Copy_${file.name}")
        file.copyTo(destFile, overwrite = true)
        refreshGrid()
        Toast.makeText(this, "File disalin", Toast.LENGTH_SHORT).show()
    }

    private fun cutFile(file: File) {
        val destFile = File(currentDir, "Cut_${file.name}")
        file.copyTo(destFile, overwrite = true)
        if (file.delete()) {
            refreshGrid()
            Toast.makeText(this, "File dipindahkan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal memindahkan", Toast.LENGTH_SHORT).show()
        }
    }
}