package com.example.speechify.repository

import android.util.Log
import com.example.speechify.R
import com.example.speechify.data.SongModel
import com.example.speechify.db.service.SongService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.kotlin.addTo
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songService: SongService
) : BaseRepository() {

    fun updateDatabase() {
        val songList = listOf(
            SongModel(
                1,
                "Fleetwood Mac Dreams",
                "Fleetwood Mac |  Rumours | 1977",
                "fleetwood_mac_dreams",
                R.raw.fleetwood_mac_dreams,
                R.drawable.dreams
            ),
            SongModel(
                2,
                "Heart Alone",
                "Heart | Bad Animals | 1987",
                "heart_alone",
                R.raw.heart_alone,
                R.drawable.alone
            ),
            SongModel(
                3,
                "Let The Bodies Hit The Floor Alone",
                "Drowning Pool |  Sinner | 2001",
                "let_the_bodies_hit_the_floor",
                R.raw.let_the_bodies_hit_the_floor,
                R.drawable.bodies
            ),
            SongModel(
                4,
                "Masked Wolf Astronaut In The Ocean",
                "Masked Wolf | Astronaut In The Ocean | 2019",
                "masked_wolf_astronaut_in_the_ocean",
                R.raw.masked_wolf_astronaut_in_the_ocean,
                R.drawable.astronut
            ),
            SongModel(
                5,
                "Nickelback Rockstar",
                "Nickelback | All the Right Reasons |2005",
                "nickelback_rockstar",
                R.raw.nickelback_rockstar,
                R.drawable.rockstrar
            ),
            SongModel(
                6,
                "Stevie Nicks Stand Back",
                "Stevie Nicks | Bella Donna (2016 Remastered) | 1981",
                "stevie_nicks_stand_back",
                R.raw.stevie_nicks_stand_back,
                R.drawable.edge_of_seventeen
            ),
            SongModel(
                7,
                "The Weeknd Blinding Lights",
                "The Weeknd | Bella Donna (2016 Remastered) | 1981",
                "the_weeknd_blinding_lights",
                R.raw.the_weeknd_blinding_lights,
                R.drawable.binding_lights
            ),
            SongModel(
                8,
                "Zombie The Cranberries",
                "The Cranberries | Everybody Else Is Doing It, So Why Can't We? | 1993",
                "zombie_the_cranberries",
                R.raw.zombie_the_cranberries,
                R.drawable.zom
            ),
        )

        songService.getSongsSingle()
            .flatMapCompletable {
                if (it.isEmpty())
                    songService.insert(songList)
                else
                    Completable.fromAction {
                        Log.d(TAG, "Database is already updated")
                    }
            }
            .subscribe({
                Log.d(TAG, "updateDatabase: complete")
            }, Throwable::printStackTrace)
            .addTo(compositeDisposable)
    }

    fun getSongs() = songService.getSongs()

    fun getSongDetails(songId: Int) = songService.getSongDetails(songId)

    companion object {
        private const val TAG = "MainActivityRepository"
    }
}