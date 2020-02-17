package com.reactive.trach.beautyphotoapp

import androidx.fragment.app.Fragment
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailFragment
import com.reactive.trach.beautyphotoapp.albums.AlbumDetailParams
import com.reactive.trach.beautyphotoapp.main.MainFragment
import com.reactive.trach.beautyphotoapp.main.MainParam

class FragmentFactory {
    companion object {
        fun create(param: SceneParam): Fragment?{
            if(param is MainParam){
                return MainFragment.instance(param)
            }
            if(param is AlbumDetailParams){
                return AlbumDetailFragment.instance(param)
            }
            return null
        }
    }
}