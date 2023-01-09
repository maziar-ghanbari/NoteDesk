package ghanbari.maziar.notedesk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ghanbari.maziar.notedesk.R
import ghanbari.maziar.notedesk.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        setSupportActionBar(binding?.toolbar)
    }

    override fun onNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}