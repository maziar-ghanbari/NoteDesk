package ghanbari.maziar.notedesk.utils

const val FOLDERS_TABLE_NAME = "folders_table"
const val NOTE_TABLE_NAME = "note_table"
const val NOTE_DESK_DATABASE_NAME = "note_desk_database"

//folder columns
const val FOLDER_ID = "folder_id"
const val FOLDER_IMG = "folder_img"
const val FOLDER_TITLE = "folder_title"

//note columns
const val NOTE_ID = "note_id"
const val NOTE_TITLE = "note_title"
const val NOTE_DES = "note_des"
const val NOTE_FOLDER_ID = "note_folder_id"
const val NOTE_DATE = "note_date"
const val NOTE_TIME = "note_time"
const val NOTE_PRIORITY = "note_priority"
const val NOTE_IS_PINNED = "note_is_pinned"

//data store
const val DATA_STORE = "data_store"
const val IS_FIRST_LAUNCH_DATA_STORE = "is_first_launch_data_store"
//log
const val TAG = "tagetag3"
enum class PriorityNote(){
    HIGH,
    NORMAL,
    LOW
}
//arg id for update
const val ARG_ID_NOTE_UPDATE = "note_id_update"
const val ARG_ID_FOLDER_UPDATE = "folder_id_update"
const val ARG_TITLE_FOLDER_UPDATE = "folder_title_update"
const val ARG_ICON_FOLDER_UPDATE = "folder_icon_update"
//folder edit delete
const val EDIT_FOLDER = "edit_folder"
const val DELETE_FOLDER = "delete_folder"
const val ADD_FOLDER = "افزودن\u200Cپوشه"
const val NO_FOLDER = "بی\u200Cپوشه\u200Cها"
const val ALL_FOLDER = "همه"
const val MAX_OF_NUMBER_OF_FOLDERS = 9