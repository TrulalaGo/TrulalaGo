package gas.trulala.trulalago

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.net.Uri
import android.widget.*
import android.database.Cursor
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.InputStreamReader
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher

class Editor : AppCompatActivity() {
    private val REQUEST_CODE = 100
    private lateinit var editText: EditText
    private lateinit var nomor: TextView
    private lateinit var scrollEdit: ScrollView
    private lateinit var scrollNomor: ScrollView
    private lateinit var memo: GridLayout
    private lateinit var popupWindow: PopupWindow
    private lateinit var listViewSuggestions: ListView
    private val suggestions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor)

        editText = findViewById(R.id.edit)
        nomor = findViewById(R.id.nomor)
        scrollEdit = findViewById(R.id.scrollEdit)
        scrollNomor = findViewById(R.id.scrollNomor)
        memo = findViewById(R.id.memo)

        val popupView = LayoutInflater.from(this).inflate(R.layout.kotak_list, null)
        listViewSuggestions = popupView.findViewById(R.id.isi)

        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, suggestions)
        listViewSuggestions.adapter = adapter

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                updateSuggestions(query) 
                if (suggestions.isNotEmpty()) {
                    popupWindow.showAsDropDown(editText) 
                } else {
                    popupWindow.dismiss()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        listViewSuggestions.setOnItemClickListener { _, _, position, _ ->
            val selectedSuggestion = suggestions[position]
            editText.setText(selectedSuggestion)
            popupWindow.dismiss() 
        }

        findViewById<ImageView>(R.id.nav).setOnClickListener {
            val liner = findViewById<LinearLayout>(R.id.liner)
            liner.visibility = if (liner.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        findViewById<ImageView>(R.id.folder).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_CODE)
        }

        findViewById<ImageView>(R.id.simpan).setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                Toast.makeText(this, "Tersimpan: $text", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Teks tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val lines = s?.toString()?.split("\n")?.size ?: 1
                nomor.text = (1..lines).joinToString("\n")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        scrollEdit.viewTreeObserver.addOnScrollChangedListener {
            nomor.scrollY = scrollEdit.scrollY
        }
    }

    private fun updateSuggestions(query: String) {
        suggestions.clear()

        if (query.isNotEmpty()) {
            val sampleSuggestions = listOf("satu", "dua", "tiga", "empat", "lima")
            suggestions.addAll(sampleSuggestions.filter { it.contains(query, ignoreCase = true) })
        }

        (listViewSuggestions.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }

    private fun isFile(uri: Uri): Boolean {
        return uri.toString().contains(".")
    }

    private fun listFilesInFolder(uri: Uri) {
        memo.removeAllViews()

        val documentFile = DocumentFile.fromTreeUri(this, uri)
        if (documentFile != null && documentFile.isDirectory) {
            for (file in documentFile.listFiles()) {
                val tampilan = LayoutInflater.from(this).inflate(R.layout.memo_horizontal, memo, false)
                val gambar = tampilan.findViewById<ImageView>(R.id.gambar)
                val nama = tampilan.findViewById<TextView>(R.id.nama)

                gambar.setImageResource(if (file.isDirectory) R.drawable.folder else R.drawable.file)
                nama.text = file.name

                memo.addView(tampilan)

                tampilan.setOnClickListener {
                    if (file.isDirectory) {
                        listFilesInFolder(file.uri)
                    } else {
                        readFileContent(file.uri)
                    }
                }
            }
        }
    }

    private fun readFileContent(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
                editText.setText(stringBuilder.toString())
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            listFilesInFolder(uri)

            val tampilan = LayoutInflater.from(this).inflate(R.layout.memo_horizontal, memo, false)
            val gambar = tampilan.findViewById<ImageView>(R.id.gambar)
            val nama = tampilan.findViewById<TextView>(R.id.nama)

            gambar.setImageResource(if (isFile(uri)) R.drawable.file else R.drawable.folder)
            nama.text = uri.lastPathSegment

            memo.addView(tampilan)

            tampilan.setOnClickListener {
                editText.setText(nama.text)
            }

            tampilan.setOnLongClickListener {
                memo.removeView(tampilan)
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}