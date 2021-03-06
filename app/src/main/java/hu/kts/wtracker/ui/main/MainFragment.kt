package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hu.kts.wtracker.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        binding.btnStart.setOnClickListener { viewModel.onStartButtonClicked() }
        binding.btnStart.setOnLongClickListener { viewModel.onStartButtonLongClicked() }
        binding.btnNotificationFrequency.setOnClickListener { viewModel.onNotificationFrequencyButtonClicked() }
        binding.twWork.setOnClickListener { viewModel.onScreenTouch() }
        binding.twRest.setOnClickListener { viewModel.onScreenTouch() }
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
            binding.root.setBackgroundColor(resources.getColor(viewState.period.color, null))
            binding.btnNotificationFrequency.text = getString(viewState.notificationFrequency.label)
            keepScreenAwake(viewState.period.isRunning())
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