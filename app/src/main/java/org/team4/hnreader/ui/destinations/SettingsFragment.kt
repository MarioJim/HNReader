package org.team4.hnreader.ui.destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team4.hnreader.R
import org.team4.hnreader.data.local.DataStoreHelper
import org.team4.hnreader.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStoreHelper: DataStoreHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreHelper = DataStoreHelper.getInstance(binding.root.context)

        binding.btnConfirmSettings.setOnClickListener {
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle(getString(R.string.change_theme))
            val styles = arrayOf(DataStoreHelper.LIGHT_THEME, DataStoreHelper.DARK_THEME)

            dataStoreHelper.currentTheme.asLiveData().observe(viewLifecycleOwner) { result ->
                val checkedTheme = if (result == DataStoreHelper.LIGHT_THEME) 0 else 1

                builder.setSingleChoiceItems(styles, checkedTheme) { dialog, which ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (which == 0)
                            AppCompatDelegate.MODE_NIGHT_NO
                        else AppCompatDelegate.MODE_NIGHT_YES
                    )
                    (activity as AppCompatActivity).delegate.applyDayNight()

                    GlobalScope.launch (Dispatchers.Main) {
                        dataStoreHelper.saveThemeConfig(if (which == 0) DataStoreHelper.LIGHT_THEME else DataStoreHelper.DARK_THEME)
                    }
                    dialog.dismiss()
                }
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}