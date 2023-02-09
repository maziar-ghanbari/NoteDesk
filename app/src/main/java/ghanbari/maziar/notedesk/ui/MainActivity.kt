package ghanbari.maziar.notedesk.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.databinding.ActivityMainBinding
import ghanbari.maziar.notedesk.utils.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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


    //init notification to show
    fun initNotification(title: String, des: String, img: Int, notificationId: Int) {
        //if permission is granted for sending notification
        //api < 33 Or (api >= 33 And PermissionGranted)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            //show notification
            showNotification(title, des, img, notificationId)
        }
        //api >= 33 And !PermissionGranted
        //request permission or show message no permission
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //request permission by register activity for result to get permission
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_RC
            )
        }
    }

    //is notification permission granted liveDada
    val isNotificationPermGrantedLiveData = MutableLiveData<Boolean>()


    //show notification straightly
    private fun showNotification(title: String, des: String, img: Int, notificationId: Int) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(img)
            .setContentTitle(title)
            .setContentText(des)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    //is storage permission granted or note
    val isStoragePermissionGrantedLiveData = MutableLiveData<Boolean>()


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //check requests permissions
        when (requestCode) {
            NOTIFICATION_PERMISSION_RC -> {
                isNotificationPermGrantedLiveData.value =
                    (grantResults.isNotEmpty() && grantResults[0]
                            == PackageManager.PERMISSION_GRANTED)
            }
            //check external storage permissions
            STORAGE_PERMISSIONS_RC_1 -> {
                isStoragePermissionGrantedLiveData.value =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            STORAGE_PERMISSIONS_RC_2 -> {
                isStoragePermissionGrantedLiveData.value =
                    grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED

            }
        }
    }

    fun exportAllNotes() {
        //if permission is ok
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            isStoragePermissionGrantedLiveData.value = true
        }
        //if it is >= api 30 and need request
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSIONS_RC_1
            )
        }
        //if api is in 23 and 29 and need request
        else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSIONS_RC_2
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}