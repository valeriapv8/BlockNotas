package com.ami.appnotas.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ami.appnotas.R
import com.ami.appnotas.databinding.ActivityAddNoteBinding
import com.ami.appnotas.models.Note
import com.ami.appnotas.repositories.NoteRepositories

class AddNoteActivity : AppCompatActivity() {
    private var note: Note? = null //Nota a editar o null si es una nueva
    private lateinit var binding: ActivityAddNoteBinding

    private val colorMap = mapOf(
        "Rojo" to "#FF0000",
        "Verde" to "#00FF00",
        "Azul" to "#0000FF",
        "Amarillo" to "#FFFF00",
        "Blanco" to "#FFFFFF",
        "Cafe" to "#8B4513",
        "Gris" to "#808080",
        "Morado" to "#800080",
        "Rosa" to "#FFC0CB",
        "Naranja" to "#FFA500"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val colorAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.colors_array, //se hace referencia al string.xml, aqui se cargan los colores anteriores
            android.R.layout.simple_spinner_item //Establece el diseño del elemento del spinner
        )
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) //Establece el diseño del menú desplegable
        binding.spColor.adapter = colorAdapter //Asocia el adaptador al spinner

        note = intent.getSerializableExtra(PARAM_NOTE) as Note? //constante que almacena la clave del intent
        loadNoteDetails(note)
        setupEventListeners()
    }

    private fun loadNoteDetails(note: Note?) {
        if (note == null) {
            return
        }
        binding.etTitle.setText(note.title)
        binding.etDescription.setText(note.description)

        val colorName = colorMap.entries.find { it.value == note.color }?.key //obtiene el nombre del color
        val index = resources.getStringArray(R.array.colors_array).indexOf(colorName) //lo busca en el array de colores
        if (index >= 0) {
            binding.spColor.setSelection(index) //si lo encuentra, lo selecciona
        }
    }

    private fun setupEventListeners() {
        binding.btnSaveNote.setOnClickListener { saveNote() }
        binding.btnCancelNote.setOnClickListener { finish() }
    }

    private fun saveNote() {
        val selectedColorName = binding.spColor.selectedItem.toString() //obtiene el nombre del color seleccionado
        val selectedColorHex = colorMap[selectedColorName] ?: "#FFFFFF" //obtiene el valor hexadecimal si no lo encuentra usa blanco

        val newNote = Note(
            note?.id ?: generateId(), //si no existe la nota, genera un nuevo id
            binding.etTitle.text.toString(),
            binding.etDescription.text.toString(),
            selectedColorHex, //guarda el color en formato hexadecimal
            note?.isCompleted ?: false
        )
        NoteRepositories.saveNote(newNote) //guarda la nota en el repositorio
        finish()
    }

    private fun generateId(): Int {
        return NoteRepositories.getNote().maxOfOrNull { it.id }?.plus(1) ?: 1
    } //obtiene todas las notas y busca la de mayor id, le suma 1 y lo devuelve, si no hay notas devuelve 1

    companion object {
        const val PARAM_NOTE = "note" //clave para pasar la nota entre actividades

        fun detailIntent(context: Context, note: Note? = null): Intent? { //devuelve un intent para abrir AddNoteActivity
            val intent = Intent(context, AddNoteActivity::class.java)
            intent.putExtra(PARAM_NOTE, note) //Si se pasa una nota, se adjunta con el putExtra
            return intent
        }
    }
}
