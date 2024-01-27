package com.example.lesson47

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.lesson47.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private val adapter = Adapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = adapter
        adapter.setData(
            listOf(
                ItemModel(
                    0,
                    getString(R.string.n2d),
                    getString(R.string.work),
                    getString(R.string.n2d),
                    getString(R.string.work)
                )
            )

        )



        binding.fragmentContainerView.findNavController().let { nestedNavController ->

            if (nestedNavController.currentDestination == null) {
                nestedNavController.setGraph(R.navigation.nav_graph)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}