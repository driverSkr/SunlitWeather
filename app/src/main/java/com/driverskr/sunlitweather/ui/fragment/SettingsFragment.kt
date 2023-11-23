package com.driverskr.sunlitweather.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.*
import com.driverskr.sunlitweather.ui.activity.AboutActivity
import com.driverskr.sunlitweather.ui.activity.CityManagerActivity
import com.driverskr.sunlitweather.utils.ContentUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.ui.activity.FeedBackActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cityManager = findPreference<Preference>("key_city_manager")
        cityManager?.setOnPreferenceClickListener {
            startActivity(Intent(context, CityManagerActivity::class.java))
            true
        }

        cityManager?.widgetLayoutResource = R.layout.layout_arrow_right

        findPreference<Preference>("key_theme")?.apply {
            widgetLayoutResource = R.layout.layout_arrow_right
        }

        findPreference<Preference>("key_feedback")?.apply {
            setOnPreferenceClickListener {
                FeedBackActivity.startActivity(requireContext())
                true
            }
            widgetLayoutResource = R.layout.layout_arrow_right
        }

        findPreference<Preference>("key_about")?.apply {
            setOnPreferenceClickListener {
                startActivity(Intent(context, AboutActivity::class.java))
                true
            }
            widgetLayoutResource = R.layout.layout_arrow_right
        }

//        val lanCategory = findPreference<PreferenceCategory>("key_lan_group")!!
        val unitCategory = findPreference<PreferenceCategory>("key_unit_group")!!

//        initState(lanCategory)
        initState(unitCategory)

        val unitHua = findPreference<CheckBoxPreference>("key_unit_hua")!!
        val unitShe = findPreference<CheckBoxPreference>("key_unit_she")!!

        unitHua.setOnPreferenceClickListener {
            changeState(unitCategory, it as CheckBoxPreference)

            changeUnit("hua")
            true
        }

        unitShe.setOnPreferenceClickListener {
            changeState(unitCategory, it as CheckBoxPreference)

            changeUnit("she")
            true
        }

        /*val lanSys = findPreference<CheckBoxPreference>("key_lan_system")!!
        val lanCn = findPreference<CheckBoxPreference>("key_lan_cn")!!
        val lanEn = findPreference<CheckBoxPreference>("key_lan_en")!!

        lanSys.setOnPreferenceClickListener {
            changeState(lanCategory, it as CheckBoxPreference)

            changeLang("sys")
            true
        }

        lanCn.setOnPreferenceClickListener {
            changeState(lanCategory, it as CheckBoxPreference)

            changeLang("zh")
            true
        }

        lanEn.setOnPreferenceClickListener {
            changeState(lanCategory, it as CheckBoxPreference)

            changeLang("en")
            true
        }*/
    }

    /**
     * 初始化可选状态
     */
    private fun initState(category: PreferenceCategory) {
        val childCount = category.preferenceCount
        for (i in 0 until childCount) {
            val item = category.getPreference(i) as CheckBoxPreference
            item.isSelectable = !item.isChecked
        }
    }

    /**
     * 改变可选及选中状态
     */
    private fun changeState(category: PreferenceCategory, target: CheckBoxPreference) {
        val childCount = category.preferenceCount
        target.isSelectable = false
        for (i in 0 until childCount) {
            val other = category.getPreference(i) as CheckBoxPreference
            if (other.key != target.key) {
                other.isChecked = false
                other.isSelectable = true
            }
        }
    }

    /**
     * 修改单位
     */
    private fun changeUnit(unit: String) {
//        ContentUtil.UNIT_CHANGE = true
        ContentUtil.APP_SETTING_UNIT = unit

        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString("unit", unit)
            apply()
        }
    }

    /**
     * 修改语言
     */
    private fun changeLang(lan: String) {
//        ContentUtil.APP_SETTING_LANG = lan
//        ContentUtil.CHANGE_LANG = true

        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putString("lan", lan)
            apply()
        }
    }


}