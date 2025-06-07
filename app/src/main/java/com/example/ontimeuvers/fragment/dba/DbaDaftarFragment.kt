package com.example.ontimeuvers.fragment.dba

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R

class DbaDaftarFragment : Fragment() {

    private lateinit var daftarMahasiswaTable : TableLayout
    private lateinit var simpanDaftarButton : Button

    private fun initComponents(view: View){
        daftarMahasiswaTable = view.findViewById(R.id.daftarMahasiswaTable)
        simpanDaftarButton = view.findViewById(R.id.simpanDaftarButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_daftar_matkul, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        val parent = parentFragment as? DbaMatkulFragment
        parent?.let { par ->
            simpanDaftarButton.setOnClickListener {
                requireParentFragment().childFragmentManager.popBackStack()
                par.focusLinearLayout.visibility = View.GONE
                par.refreshTable()
            }

            for (data in par.daftarUser){
                val tableRow = TableRow(requireContext())

                val textNim = TextView(requireContext()).apply {
                    text = data.nim
                    setPadding(8, 8, 8, 8)
                    setTypeface(null, Typeface.NORMAL)
                    layoutParams = TableRow.LayoutParams(dpToPx(110), TableRow.LayoutParams.WRAP_CONTENT)
                    setBackgroundResource(R.drawable.border_table)
                }

                val textNama = TextView(requireContext()).apply {
                    text = data.name
                    setPadding(8, 8, 8, 8)
                    setTypeface(null, Typeface.NORMAL)
                    layoutParams = TableRow.LayoutParams(dpToPx(190), TableRow.LayoutParams.WRAP_CONTENT)
                    setBackgroundResource(R.drawable.border_table)
                }

                val cekLinear = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.border_table)
                    layoutParams = TableRow.LayoutParams(dpToPx(60), TableRow.LayoutParams.WRAP_CONTENT)

                    val cek = CheckBox(requireContext()).apply {
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setOnCheckedChangeListener(null)
                        isChecked = true
                        setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                if (!par.daftarUser.contains(data)) {
                                    par.daftarUser.add(data)
                                }
                            } else {
                                par.daftarUser.remove(data)
                            }
                        }
                    }
                    addView(cek)
                }
                tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val maxHeight = listOf(textNim, textNama, cekLinear).maxOf { it.height }

                        textNim.height = maxHeight
                        textNama.height = maxHeight
                        cekLinear.layoutParams.height = maxHeight
                    }
                })

                tableRow.addView(textNim)
                tableRow.addView(textNama)
                tableRow.addView(cekLinear)

                daftarMahasiswaTable.addView(tableRow)
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