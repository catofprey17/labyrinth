package ru.c17.labyrinth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.c17.labyrinth.databinding.FragmentGameBinding
import ru.c17.labyrinth.engine.GameAdapter
import ru.c17.labyrinth.fragment.viewBinding

class GameFragment : Fragment() {

    private val binding by viewBinding(FragmentGameBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gameLayout.setAdapter(GameAdapter(requireContext()))
        binding.gameLayout.runGame()
    }
}