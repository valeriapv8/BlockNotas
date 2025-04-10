package com.ami.appnotas.repositories

import com.ami.appnotas.models.Note

object NoteRepositories { //para acceder a sus funcionamientos de manera global
    private val arrayNote = arrayListOf(
        Note(1, "Nota 1", "Descripción de la nota 1", "#FF0000", false),
        Note(2, "Nota 2", "Descripción de la nota 2", "#00FF00", false),
        Note(3, "Nota 3", "Descripción de la nota 3", "#0000FF", false),
        Note(4, "Nota 4", "Descripción de la nota 4", "#FFFF00", false),
        Note(5, "Nota 5", "Descripción de la nota 5", "#FF00FF", false),
        Note(6, "Nota 6", "Descripción de la nota 6", "#00FFFF", false),
    )
    fun getNote(): ArrayList<Note> {
        return arrayNote.clone() as ArrayList<Note> //devuelve una copia de la lista de notas
    } //el exterior no puede modificar la lista interna del repositorio

    fun saveNote(note: Note) {
        val index = arrayNote.indexOfFirst { it.id == note.id } //busca el índice de la nota por su id
        if (index >= 0) {
            arrayNote[index] = note //actualiza la nota existente la reemplaza
        } else {
            arrayNote.add(note) //agrega una nueva nota
        }
    }

    fun deleteNote(id: Int) {
        arrayNote.removeIf { it.id == id } //busca todas las notas cuyo id coincida con el parametro y la elimina
    }
}