package com.example.ontimeuvers.fragment.dba

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.AddUsersMatkul
import com.example.ontimeuvers.repository.JurusanRepositoryImpl
import com.example.ontimeuvers.repository.MatkulRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.DBAServiceImpl

class DbaValidationFragment : Fragment() {

    private val dbaService = DBAServiceImpl(UserRepositoryImpl(), MatkulRepositoryImpl(), JurusanRepositoryImpl())
    private lateinit var closeValidationMatkulImageButton : ImageButton
    private lateinit var confirmAddMatkulButton : Button
    private lateinit var daftarBtn : Button
    private lateinit var matkulBtn : Button

    private fun initComponents(view: View){
        closeValidationMatkulImageButton = view.findViewById(R.id.closeValidationMatkulImageButton)
        confirmAddMatkulButton = view.findViewById(R.id.confirmAddMatkulButton)
        daftarBtn = view.findViewById(R.id.daftarBtn)
        matkulBtn = view.findViewById(R.id.matkulBtn)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_validasi_matkul, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        closeValidationMatkulImageButton.setOnClickListener {
            requireParentFragment().childFragmentManager.popBackStack()
        }

        val daftarFragment = DbaTableDaftarFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.dbaValidationContainer, daftarFragment)
            .commit()

        daftarBtn.setOnClickListener {
            daftarBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.whites))
            matkulBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            val daftarFragments = DbaTableDaftarFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.dbaValidationContainer, daftarFragments)
                .commit()
        }
        matkulBtn.setOnClickListener {
            daftarBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            matkulBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.whites))
            val matkulFragments = DbaTableMatkulFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.dbaValidationContainer, matkulFragments)
                .commit()
        }

        val parent = parentFragment as? DbaMatkulFragment
        parent?.let { per ->
            confirmAddMatkulButton.setOnClickListener {
                val request = AddUsersMatkul(per.daftarUser, per.jadwal)
                dbaService.addUsersMatkul(request).thenAccept { userError ->
                    requireActivity().runOnUiThread {
                        if (userError.users.isEmpty()){
                            requireParentFragment().childFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            per.focusLinearLayout.visibility = View.GONE
                            Toast.makeText(requireContext(), "Success add matkul to all users", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.exceptionally { ex ->
                    Toast.makeText(requireContext(), "Failed: ${ex.message}", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        }
    }

}