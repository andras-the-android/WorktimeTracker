package hu.kts.wtracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.button.setOnClickListener { viewModel.onButtonClicked() }
        binding.button.setOnLongClickListener { viewModel.onButtonLongClicked() }
        binding.root.setOnClickListener { viewModel.onScreenTouch() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { viewState ->
            binding.twWork.text = viewState.work
            binding.twRest.text = viewState.rest
            binding.button.text = viewState.buttonText
            val backgroundColor = when {
                !viewState.isRunning -> R.color.bg_default
                viewState.period == MainViewModel.Period.WORK -> R.color.bg_work
                else -> R.color.bg_rest
            }
            binding.root.setBackgroundColor(resources.getColor(backgroundColor, null))
        }
    }

}