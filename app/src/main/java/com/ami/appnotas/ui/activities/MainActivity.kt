package com.ami.appnotas.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ami.appnotas.R
import com.ami.appnotas.databinding.ActivityMainBinding
import com.ami.appnotas.models.Note
import com.ami.appnotas.repositories.NoteRepositories
import com.ami.appnotas.ui.adapters.NotesAdapter

class MainActivity : AppCompatActivity(), NotesAdapter.NoteClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupEventListeners()
    }

    private fun setupEventListeners() {
        binding.fabAddNote.setOnClickListener {
            val intent = AddNoteActivity.detailIntent(this)
            startActivity(intent) //Iniciar la actividad de agregar nota al hacer clic en el botón flotante
        }
    }

    override fun onResume() {
        super.onResume() //Recargar los datos cada que esta pantalla vuelva ser visible
        reloadData()
    }

    private fun reloadData() {
        val note = NoteRepositories.getNote()
        val adapter = binding.rvNotesList.adapter as NotesAdapter //Toma las notas del repositorio y se las pasa a adapter
        adapter.setData(note) //Obtiene la lista y actualiza el adaptador con la lista de notas
    }

    private fun setupRecyclerView() { // se necesita crear un adapter vacio para usar despues setData
        val adapter = NotesAdapter(arrayListOf()) //Crear una instancia vacia del adaptador de notas
        binding.rvNotesList.apply {
            this.adapter = adapter  //se asocia el adaptador al recyclerView
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        adapter.setOnNoteClickListener(this) //Establecer el listener de clic en el adaptador
    }

    override fun onNoteClick(note: Note) {
        Toast.makeText(this, "Tarea ${note.title} marcada como ${if (note.isCompleted) "completada" else "pendiente"}", Toast.LENGTH_SHORT).show()
    }

    override fun onNoteDetailClick(note: Note) { //Abre la actividad de detalles de la nota al hacer clic en la nota
        val intent = AddNoteActivity.detailIntent(this, note)
        startActivity(intent)
    }

    override fun onNoteDeleted() {
        Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show()
        reloadData()
    }
}
