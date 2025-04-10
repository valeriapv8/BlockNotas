package com.ami.appnotas.models

import java.io.Serializable

class Note (
    val id: Int,
    val title: String,
    val description: String,
    val color: String,
    var isCompleted: Boolean = false
): Serializable //permite ser serializable, util para pasar datos entre actividades