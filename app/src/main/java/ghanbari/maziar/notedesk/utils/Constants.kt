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
const val DATA_STORE_TITLE_NOTE = "data_store_title_note"
const val DATA_STORE_DES_NOTE = "data_store_des_note"
//log
const val TAG = "tagetag3"
enum class PriorityNote(){
    HIGH,
    NORMAL,
    LOW
}
//arg id for update
const val ARG_ID_NOTE_UPDATE = "note_id_update"
const val ARG_ID_NOTE_SHOW = "note_id_show"
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

//open close searchBox and slideMenu(folders) by animation
enum class SearchFolderAnimState(){
    SEARCH_BOX_OPEN,
    SLIDE_MENU_OPEN,
    ALL_OPEN,
    ALL_CLOSE
}
//notification channel
const val CHANNEL_ID = "channel_id"
//permission request code
const val NOTIFICATION_PERMISSION_RC = 1022
//API >= 30
const val STORAGE_PERMISSIONS_RC_1 = 1023
//API < 30 & API > M
const val STORAGE_PERMISSIONS_RC_2 = 1024