package com.example.ontimeuvers.fragment.dba

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.entity.User
import com.example.ontimeuvers.model.FindMahasiswaRequest
import com.example.ontimeuvers.repository.JurusanRepositoryImpl
import com.example.ontimeuvers.repository.MatkulRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.DBAServiceImpl
import java.util.ArrayList

class DbaMatkulFragment : Fragment() {

    private val dbaService = DBAServiceImpl(UserRepositoryImpl(), MatkulRepositoryImpl(), JurusanRepositoryImpl())
    private lateinit var request : FindMahasiswaRequest
    private lateinit var spinnerJurusan : Spinner
    private lateinit var spinnerAngkatan : Spinner
    private lateinit var searchEditText: EditText
    private lateinit var tableMahasiswa: TableLayout
    private lateinit var refreshButton: ImageButton
    private lateinit var backImageButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isSpinnerInitialized = false
    private var isLoading = false
    lateinit var focusLinearLayout: ConstraintLayout

    private lateinit var daftarMahasiswaButton: Button
    lateinit var editMatkulFormButton : Button

    var daftarUser: ArrayList<User> = arrayListOf()
    val jadwal = mutableMapOf<String, Map<String, String>>()

    private fun initComponents(view: View){
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)
        spinnerAngkatan = view.findViewById(R.id.spinnerAngkatan)
        searchEditText = view.findViewById(R.id.searchEditText)
        tableMahasiswa = view.findViewById(R.id.tableMahasiswa)
        refreshButton = view.findViewById(R.id.refreshButton)
        request = FindMahasiswaRequest()
        backImageButton = view.findViewById(R.id.backImageButton)
        focusLinearLayout = view.findViewById(R.id.focusLinearLayout)
        daftarMahasiswaButton = view.findViewById(R.id.daftarMahasiswaButton)
        editMatkulFormButton = view.findViewById(R.id.editMatkulFormButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_matkul_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        back()

        requestData()

        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            dbaService.clearCache()
            getTable(request)
        }

        getDaftar()
        getEditMatkul()
    }

    private fun getDaftar(){
        daftarMahasiswaButton.setOnClickListener {
            focusLinearLayout.visibility = View.VISIBLE
            val daftarFragment = DbaDaftarFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.dbaMatkulContainer, daftarFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getEditMatkul(){
        editMatkulFormButton.setOnClickListener {
            focusLinearLayout.visibility = View.VISIBLE
            val daftarFragment = DbaAddMatkulFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.dbaMatkulContainer, daftarFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun requestData(){
        val jurusan = mutableListOf("Pilih Jurusan")

        val angkatan = mutableListOf("Pilih Angkatan")

        dbaService.allJurusan().thenAccept { allJurusan ->
            requireActivity().runOnUiThread {
                val jurusanList = allJurusan.map { it.nama }.distinct().sorted()
                jurusan.addAll(jurusanList)

                val adapterJurusan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    jurusan
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerJurusan.adapter = it
                }

                val angkatanList = allJurusan.mapNotNull { it.angkatan }.distinct().sorted()
                angkatan.addAll(angkatanList.map { it.toString() })

                val adapterAngkatan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    angkatan
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAngkatan.adapter = it
                }
                spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
                        if (isSpinnerInitialized) getTable(request)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        request.jurusan = null
                    }
                }

                spinnerAngkatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        request.angkatan = if (position == 0) null else (parent.getItemAtPosition(position).toString().toIntOrNull())
                        if (isSpinnerInitialized) getTable(request)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        request.angkatan = null
                    }
                }
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    request.name = s.toString()
                    getTable(request)
                }

                handler.postDelayed(searchRunnable!!, 800)
            }
        })

        handler.postDelayed({
            isSpinnerInitialized = true
            getTable(request)
        }, 300)
    }

    fun refreshTable(){
        getTable(request)
    }

    private fun getTable(request: FindMahasiswaRequest){
        dbaService.searchMahasiswa(request).thenAccept { datas ->
            requireActivity().runOnUiThread {
                tableMahasiswa.removeAllViews()
                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textNim = TextView(requireContext()).apply {
                        text = data.nim
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(120), TableRow.LayoutParams.WRAP_CONTENT)
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
                            isChecked = daftarUser.contains(data)

                            setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    daftarUser.add(data)
                                } else {
                                    daftarUser.remove(data)
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

                    tableMahasiswa.addView(tableRow)
                }
                isLoading = false
                refreshButton.isEnabled = true
            }
        }.exceptionally {
            requireActivity().runOnUiThread {
                isLoading = false
                refreshButton.isEnabled = true
            }
            null
        }
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
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