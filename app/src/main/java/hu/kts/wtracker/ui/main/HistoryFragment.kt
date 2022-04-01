package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hu.kts.wtracker.R
import hu.kts.wtracker.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentHistoryBinding
    private var density = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        density = requireContext().resources.displayMetrics.density
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater)

        //TODO solve crash
//        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { view, windowInsets ->
//            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            layoutParams.setMargins(insets.left, insets.left, insets.right, insets.bottom)
//            view.layoutParams = layoutParams
//
//            WindowInsetsCompat.CONSUMED
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.historyState.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.container.removeAllViews()
            } else if (binding.container.childCount > 0) {
                //if the container is already populated, then it enough to add the latest item
                binding.container.addView(generateView(items.last))
            } else {
                for (item in items) binding.container.addView(generateView(item))
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), state.period.color))
        }
    }

    private fun generateView(viewItem: MainViewModel.PeriodHistoryViewItem): View {
        return TextView(requireActivity()).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, calcHeight(viewItem.duration))
            setBackgroundColor(ContextCompat.getColor(requireContext(), viewItem.color))
            text = requireContext().getString(R.string.history_item_text, viewItem.timestamp, viewItem.duration)
            maxLines = 1
            gravity = Gravity.CENTER
            TextViewCompat.setAutoSizeTextTypeWithDefaults(this ,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this, 1, 64, 4, COMPLEX_UNIT_PX)
        }
    }

    private fun calcHeight(duration: Int): Int {
        //0 duration is in fact 0 - 30s so we set the height to half of a minute to make it visible on the screen
        val heightInDp = if (duration == 0) DPS_PER_MINUTE / 2 else duration * DPS_PER_MINUTE
        return (heightInDp * density).toInt()
    }

    companion object {
        const val DPS_PER_MINUTE = 4
    }


}