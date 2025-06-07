package com.example.ontimeuvers.fragment.dba

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R

class DbaTableMatkulFragment : Fragment() {

    private lateinit var tableMatkulTableLayout : TableLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_table_matkul_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableMatkulTableLayout = view.findViewById(R.id.tableMatkulTableLayout)

        val parent = requireParentFragment().requireParentFragment() as? DbaMatkulFragment

        tableMatkulTableLayout.removeAllViews()

        parent?.jadwal?.forEach { (day, session) ->
            val row = TableRow(requireContext())

            val label = TextView(requireContext()).apply {
                text = day
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.border_table)
                layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
            }
            row.addView(label)

            session.forEach { (sesi, matkul) ->
                val sesiTextView = TextView(requireContext()).apply {
                    text = matkul
                    setPadding(8, 8, 8, 8)
                    setTypeface(null, Typeface.NORMAL)
                    layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
                    setBackgroundResource(R.drawable.border_table)
                }
                row.addView(sesiTextView)
            }

            tableMatkulTableLayout.addView(row)
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