package ghanbari.maziar.notedesk.ui.main

import android.util.Log
import ghanbari.maziar.notedesk.data.model.NoteAndFolder
import ghanbari.maziar.notedesk.utils.TAG

//****notes entity list operators****

//get notes by title search
fun MutableList<NoteAndFolder>.byTitleSearch(title: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.title.contains(title, true)) {
            result.add(it)
        }
    }
    return result
}

//get notes by folder search
fun MutableList<NoteAndFolder>.byFolderSearch(folder: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.folder.title.contains(folder, true)) {
            result.add(it)
        }
    }
    return result
}

//get note by id search
fun MutableList<NoteAndFolder>.byIdSearch(id: Int): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.id == id) {
            result.add(it)
            return result
        }
    }
    return result
}

//get notes by priority search
fun MutableList<NoteAndFolder>.byPrioritySearch(priority: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        if (it.note.priority.contains(priority, true)) {
            result.add(it)
        }
    }
    return result
}

//get notes by pin search
fun MutableList<NoteAndFolder>.orderByPinSearch(): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    result.addAll(this)
    result.sortBy {
        it.note.isPinned
    }
    Log.e(TAG, "orderByPinSearch: $result\n$this")
    return result
}

//get notes by date search
fun MutableList<NoteAndFolder>.byDateSearch(date: String): MutableList<NoteAndFolder> {
    val result = mutableListOf<NoteAndFolder>()
    this.forEach {
        Log.e(TAG, "byDateSearch:$date " + it.note.date)
        if (it.note.date.contains(date, true)) {
            result.add(it)
        }
    }
    return result
}
