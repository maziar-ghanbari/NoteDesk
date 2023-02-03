package ghanbari.maziar.notedesk.data.repository

import ghanbari.maziar.notedesk.data.local.FolderDao
import ghanbari.maziar.notedesk.data.model.FolderEntity
import ghanbari.maziar.notedesk.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddEditFolderRepository @Inject constructor(private val folderDao : FolderDao) {



    //folders
    fun getAllFolders() = flow<MyResponse<MutableList<FolderEntity>>> {
        folderDao.getAllFolders().collect {
            //no empty note declared in kharazmi
            emit(MyResponse.success(it))
        }
    }.flowOn(Dispatchers.IO)
        .catch { emit(MyResponse.error(it.message.toString())) }

    //insertFolder
    suspend fun insertFolder(folder: FolderEntity) = folderDao.insertFolder(folder)

    //updateFolder
    suspend fun updateFolder(folder: FolderEntity) = folderDao.updateFolder(folder)

}
