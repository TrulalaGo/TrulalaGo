
package gas.trulala.trulalago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.view.View
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<ImageView>(R.id.nav).setOnClickListener{
        val liner = findViewById<LinearLayout>(R.id.liner)
        liner.visibility = if(liner.visibility == View.GONE)View.VISIBLE else View.GONE}
        
        findViewById<LinearLayout>(R.id.editor).setOnClickListener{
        startActivity(Intent(this, Editor::class.java))}
        
        findViewById<LinearLayout>(R.id.berkas).setOnClickListener{
        startActivity(Intent(this, Berkas::class.java))}
        
        findViewById<LinearLayout>(R.id.chating).setOnClickListener{
        startActivity(Intent(this, Chating::class.java))}
        
        findViewById<LinearLayout>(R.id.browser).setOnClickListener{
        startActivity(Intent(this, Browser::class.java))}
        
        findViewById<LinearLayout>(R.id.terminal).setOnClickListener{
        startActivity(Intent(this, Terminal::class.java))}
        
    }
}
