package com.example.ontimeuvers.fragment.dba

import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R

class DbaAddMatkulFragment : Fragment() {

    private lateinit var closeAddMatkulImageButton: ImageButton
    private lateinit var addMatkulAllButton: Button
    private lateinit var detailMatkulTableLayout: TableLayout

    private fun initComponents(view: View){
        closeAddMatkulImageButton = view.findViewById(R.id.closeAddMatkulImageButton)
        addMatkulAllButton = view.findViewById(R.id.addMatkulAllButton)
        detailMatkulTableLayout = view.findViewById(R.id.detailMatkulTableLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_set_matkul_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        val parent = parentFragment as? DbaMatkulFragment
        parent?.let { par ->
            closeAddMatkulImageButton.setOnClickListener {
                requireParentFragment().childFragmentManager.popBackStack()
                par.focusLinearLayout.visibility = View.GONE
            }
        }

        matkul()
    }

    private fun matkul() {
        detailMatkulTableLayout.removeAllViews()
        val parent = parentFragment as? DbaMatkulFragment
        val sesiInputs = mutableMapOf<String, Pair<EditText, EditText>>()

        val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

        for (day in days){
            val row = TableRow(requireContext())

            val label = TextView(requireContext()).apply {
                text = day
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.border_table)
                layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
            }

            val sesi1 = EditText(requireContext()).apply {

                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.border_table)
                inputType = InputType.TYPE_CLASS_TEXT
                typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
                textSize = 16f
                layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
            }

            val sesi2 = EditText(requireContext()).apply {

                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.border_table)
                inputType = InputType.TYPE_CLASS_TEXT
                typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
                textSize = 16f
                layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
            }
            row.addView(label)
            row.addView(sesi1)
            row.addView(sesi2)

            detailMatkulTableLayout.addView(row)

            sesiInputs[day] = Pair(sesi1, sesi2)
        }

        if (parent != null){

            addMatkulAllButton.setOnClickListener {
                for ((day, sesiPair) in sesiInputs) {
                    val sesi1Text = sesiPair.first.text.toString()
                    val sesi2Text = sesiPair.second.text.toString()

                    parent.jadwal[day] = mapOf(
                        "sesi1" to sesi1Text,
                        "sesi2" to sesi2Text
                    )
                }
                val newFragment = DbaValidationFragment()

                requireParentFragment()
                    .childFragmentManager
                    .beginTransaction()
                    .replace(R.id.dbaMatkulContainer, newFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}