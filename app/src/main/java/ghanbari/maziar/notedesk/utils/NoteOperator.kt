package ghanbari.maziar.notedesk.utils

data class NoteOperator (val operator : Operators , val strQuery : String? = null, val intQuery : Int? = null ) {

    enum class Operators{
        NO_OPERATOR,
        BY_TITLE_SEARCH,
        BY_FOLDER_SEARCH,
        BY_ID_SEARCH,
        BY_PRIORITY_SEARCH,
        ORDER_BY_PIN_SEARCH,
        BY_DATE_SEARCH
    }

    companion object{
        fun getAllNotes() = NoteOperator(Operators.NO_OPERATOR)
        fun getByTitleSearch(title : String) = NoteOperator(Operators.BY_TITLE_SEARCH,title)
        fun getByFolderSearch(folder : String) = NoteOperator(Operators.BY_FOLDER_SEARCH,folder)
        fun getByDateSearch(date : String) = NoteOperator(Operators.BY_DATE_SEARCH,date)
        fun getByPrioritySearch(priority : String) = NoteOperator(Operators.BY_PRIORITY_SEARCH,priority)
        fun getByIdSearch(id : Int) = NoteOperator(Operators.BY_ID_SEARCH, intQuery = id)
        fun getOrderByPinSearch() = NoteOperator(Operators.ORDER_BY_PIN_SEARCH)
    }
}