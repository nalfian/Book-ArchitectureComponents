package com.toshiba.book.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.toshiba.book.view.fragment.DetailFragment
import com.toshiba.book.view.fragment.ProfilFragment

class BookPager(fm: FragmentManager) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return ProfilFragment()
            1 -> return DetailFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return FRAGMENT_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Profil"
            1 -> return "Detail"
        }
        return null
    }

    companion object {

        private val TAG = BookPager::class.java.simpleName

        private const val FRAGMENT_COUNT = 2
    }
}
