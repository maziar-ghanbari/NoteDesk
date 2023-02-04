package ghanbari.maziar.notedesk.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.databinding.ActivityMainBinding
import ghanbari.maziar.notedesk.utils.PERMISSION_REQUEST_CODE
import ghanbari.maziar.notedesk.utils.alertDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var navHostFragment: NavHostFragment
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        onBackPressedDispatcher.addCallback(this, onBackInvokedCallback)


    }

    //show alert dialog for exit app callback pressed
    private val onBackInvokedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            alertDialog(getString(R.string.exit_app_title), getString(R.string.exit_app_message)) {
                finish()
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onNavigateUp()
    }


    //is permission granted or note
    val isPermissionGrantedLiveData = MutableLiveData<Boolean>()


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }


    fun methodRequiresTwoPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isPermissionGrantedLiveData.postValue(true)
            return
        }

        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing
            isPermissionGrantedLiveData.postValue(true)
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.permission_required),
                PERMISSION_REQUEST_CODE, *perms
            )
        }
        /*
        notice :
        According to google documentation Starting in Android 11, apps cannot create
        their own app-specific directory on external storage, and also the same thing
        for creating files, so to fix this you can create the file in any other directory like
        downloads directory
        source : MoCoding - stackoverflow # it was useful for me in this project (maziar ghanbari)
        */
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //permission granted for write to external storage
        val requiredPerms = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (requestCode == PERMISSION_REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            && perms.contains(requiredPerms[0])) {
            isPermissionGrantedLiveData.postValue(true)
        }else if (requestCode == PERMISSION_REQUEST_CODE && perms.containsAll(requiredPerms)) {
            isPermissionGrantedLiveData.postValue(true)
        }else{
            isPermissionGrantedLiveData.postValue(false)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //permission deniyed
        isPermissionGrantedLiveData.postValue(false)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}