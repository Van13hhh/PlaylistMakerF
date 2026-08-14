package com.example.playlistmaker.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media.fragments.FavoriteTracksFragment
import com.example.playlistmaker.ui.media.fragments.ListOfPlaylistsFragment

class MediaPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(p0: Int): Fragment {
        return when (p0) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> ListOfPlaylistsFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}