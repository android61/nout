fun main() {
    NoteService.add(Note(title = "title 1", text = "text 1"))
    NoteService.add(Note(title = "title 2", text = "text 2"))
    println(NoteService.createComment(2, Comment(message = "comment 1")))
    NoteService.createComment(2, Comment(message = "comment 2"))
    //NoteService.deleteNote(1)
    //NoteService.restoreComment(2, 1)
    NoteService.editComment(2, 2, Comment(2, "new text"))
    println(NoteService.get())
    NoteService.print()
    println(NoteService.getComments(2))
}

class NoteNotFoundException(message: String) : RuntimeException(message)

data class Note(
    val noteId: Int? = null,
    val title: String,
    val text: String,
    val privacy: Int = 0,
    val commentPrivacy: Int = 0,
    var comments: List<Comment> = mutableListOf()
)

data class Comment(
    val commentId: Int? = null,
    var message: String,
    var delete: Boolean = false
)

object NoteService {
    private var notes = mutableListOf<Note>()
    private var lastNoteId = 0
    private var lastCommentId = 0
    const val DONE = 1

    fun add(note: Note): Note {
        notes += note.copy(noteId = ++lastNoteId)
        return notes.last()
    }

    fun createComment(noteId: Int, comment: Comment): Comment {
        for (note in notes)
            if (note.noteId == noteId) {
                note.comments += comment.copy(commentId = ++lastCommentId)
                return note.comments.last()
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun deleteNote(noteId: Int): Int {
        for (note in notes)
            if (note.noteId == noteId) {
                notes.remove(note)
                return DONE
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun deleteComment(noteId: Int, commentId: Int): Int {
        for (note in notes)
            if (note.noteId == noteId) {
                for (comment in note.comments)
                    if (comment.commentId == commentId) {
                        if (!comment.delete) {
                            comment.delete = true
                            return DONE
                        }
                        throw NoteNotFoundException("Комментарий с Id $commentId был удален ранее")
                    }
                throw NoteNotFoundException("Комментарий с Id $commentId не найден")
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun editNote(newNote: Note): Int {
        for ((index, note) in notes.withIndex())
            if (note.noteId == newNote.noteId) {
                notes[index] = newNote
                return DONE
            }
        throw NoteNotFoundException("Заметка с Id $newNote.noteId не найдена")
    }

    fun editComment(noteId: Int, commentId: Int, newComment: Comment): Int {
        for (note in notes)
            if (note.noteId == noteId) {
                for (comment in note.comments)
                    if (comment.commentId == commentId && !comment.delete) {
                        comment.message = newComment.message
                        return DONE
                    }
                throw NoteNotFoundException("Комментарий с Id $commentId был удален или не найден")
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun get(): List<Note> {
        if (notes.isNotEmpty()) {
            return notes
        }
        throw NoteNotFoundException("Заметки не найдены")
    }

    fun getById(noteId: Int): Note {
        if (!notes.none { it.noteId == noteId }) {
            return notes[notes.indexOf(notes.find { it.noteId == noteId })]
        }
        throw NoteNotFoundException("Заметки не найдены")
    }

    fun getComments(noteId: Int): List<Comment> {
        for (note in notes)
            if (note.noteId == noteId) {
                return note.comments.filter { !it.delete }
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun restoreComment(noteId: Int, commentId: Int): Int {
        for (note in notes)
            if (note.noteId == noteId) {
                for (comment in note.comments)
                    if (comment.commentId == commentId) {
                        if (comment.delete) {
                            comment.delete = false
                            return DONE
                        }
                        throw NoteNotFoundException("Комментарий с Id $commentId не был удален")
                    }
                throw NoteNotFoundException("Комментарий с Id $commentId не найден")
            }
        throw NoteNotFoundException("Заметка с Id $noteId не найдена")
    }

    fun clear() {
        notes = mutableListOf<Note>()
        lastNoteId = 0
        lastCommentId = 0
    }

    fun print() {
        for (note in notes) {
            println(note)
        }
    }
}