package com.ami.appnotas.ui.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.appnotas.databinding.NotesListBinding
import com.ami.appnotas.models.Note
import com.ami.appnotas.repositories.NoteRepositories

class NotesAdapter(
    var notes: ArrayList<Note>, //Se le pasa una lista de notas
): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var noteClickListener: NoteClickListener? = null //listener para capturar eventos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotesListBinding.inflate(
            inflater,
            parent,
            false
        )
        return ViewHolder(binding) //se ejecuta cuando se necesita crear otro viewHolder
    }

    override fun getItemCount(): Int {
        return notes.size //devuelve cuantas notas hay
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notes[position]
        holder.bind(item, noteClickListener) //se llama para rellenar cada tarjeta
    }

    fun setOnNoteClickListener(listener: NoteClickListener) {
        noteClickListener = listener
    }

    fun setData(notes: java.util.ArrayList<Note>) {
        println("Notas recibidas en adapter: ${notes.size}")
        this.notes = notes
        notifyDataSetChanged() //notifica al rv que los datos han cambiado
    }

    class ViewHolder(private val binding: NotesListBinding):
        RecyclerView.ViewHolder(binding.root) { //contenedor para una vista individual, solo una nota
        fun bind(item: Note, listener: NoteClickListener?) {
            binding.txtTitleNotes.text = item.title
            binding.txtDescriptionNotes.text = item.description

            try {
                binding.cardView.setCardBackgroundColor(Color.parseColor(item.color)) //color definido por la nota
            } catch (e: Exception) {
                binding.cardView.setCardBackgroundColor(Color.WHITE) //si falla pone blanco por defecto
            }

            binding.cbCheckNotes.setOnCheckedChangeListener(null)
            binding.cbCheckNotes.isChecked = item.isCompleted //marca la nota como completada

            val flag = Paint.STRIKE_THRU_TEXT_FLAG //si esta aplicada tacha el texto
            binding.txtTitleNotes.paintFlags = if (item.isCompleted) binding.txtTitleNotes.paintFlags or flag
            else binding.txtTitleNotes.paintFlags and flag.inv()

            binding.txtDescriptionNotes.paintFlags = if (item.isCompleted) {
                binding.txtDescriptionNotes.paintFlags or flag
            } else {
                binding.txtDescriptionNotes.paintFlags and flag.inv()
            }

            binding.cbCheckNotes.setOnCheckedChangeListener { _, isChecked ->
                val updatedNote = Note(item.id, item.title, item.description, item.color, isChecked) //crea una nueva nota con el nuevo estado
                NoteRepositories.saveNote(updatedNote) //los guarda en el repo
                listener?.onNoteClick(updatedNote)

                binding.txtTitleNotes.paintFlags = if (isChecked) {
                    binding.txtTitleNotes.paintFlags or flag
                } else {
                    binding.txtTitleNotes.paintFlags and flag.inv()
                }

                binding.txtDescriptionNotes.paintFlags = if (isChecked) {
                    binding.txtDescriptionNotes.paintFlags or flag
                } else {
                    binding.txtDescriptionNotes.paintFlags and flag.inv()
                }

                listener?.onNoteClick(updatedNote)
            }

            binding.btnDeleteNote.setOnClickListener {
                val context = binding.root.context
                androidx.appcompat.app.AlertDialog.Builder(context) //mensaje de alerta
                    .setTitle("Eliminar nota")
                    .setMessage("¿Estás seguro de que deseas eliminar esta nota?")
                    .setPositiveButton("Sí") { _, _ ->
                        NoteRepositories.deleteNote(item.id)
                        listener?.onNoteDeleted()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } //elimina la nota al hacer clic en el boton de eliminar

            binding.root.setOnClickListener {
                listener?.onNoteDetailClick(item)
            } //si se toca cualquier parte de la tarjeta, se abre el detalle de la nota
        }
    }

    interface NoteClickListener {
        fun onNoteClick(note: Note)
        fun onNoteDetailClick(note: Note)
        fun onNoteDeleted()
    }
}