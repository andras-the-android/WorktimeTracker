package hu.kts.wtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import hu.kts.wtracker.databinding.ActivityMainBinding
import hu.kts.wtracker.ui.main.HistoryFragment
import hu.kts.wtracker.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pager.adapter = MainPagerAdapter(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private inner class MainPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MainFragment()
                else -> HistoryFragment()
            }
        }
    }

    companion object {
        private const val NUM_PAGES = 2
    }
}