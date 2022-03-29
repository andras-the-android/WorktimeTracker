package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hu.kts.wtracker.R
import hu.kts.wtracker.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        binding.btnStart.setOnClickListener { viewModel.onStartButtonClicked() }
        binding.btnStart.setOnLongClickListener { viewModel.onStartButtonLongClicked() }
        binding.btnNotificationFrequency.setOnClickListener { viewModel.onNotificationFrequencyButtonClicked() }
        binding.root.setOnClickListener { viewModel.onScreenTouch() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { viewState ->
            binding.twWork.text = viewState.work
            binding.twRest.text = viewState.rest
            binding.twWorkSegment.text = viewState.workSegment
            binding.twRestSegment.text = viewState.restSegment
            binding.btnStart.text = viewState.buttonText
            val backgroundColor = when {
                !viewState.isRunning -> R.color.bg_default
                viewState.period == MainViewModel.Period.WORK -> R.color.bg_work
                else -> R.color.bg_rest
            }
            binding.root.setBackgroundColor(resources.getColor(backgroundColor, null))
            binding.btnNotificationFrequency.text = getString(viewState.notificationFrequency.label)
            keepScreenAwake(viewState.isRunning)
        }
    }

    private fun keepScreenAwake(keep: Boolean) {
        if (keep) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

}