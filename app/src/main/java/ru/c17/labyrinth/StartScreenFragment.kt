package ru.c17.labyrinth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.c17.labyrinth.databinding.FragmentStartScreenBinding
import ru.c17.labyrinth.fragment.viewBinding

class StartScreenFragment : Fragment() {

    private val binding by viewBinding(FragmentStartScreenBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPyramid.setOnPlayerPointRunnable {
            findNavController().navigate(R.id.gameFragment)
        }
    }
}