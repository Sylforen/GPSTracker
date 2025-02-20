package com.example.gpstracker

import android.app.Application
import android.location.Location

class MyApplication : Application() {

    private lateinit var myLocations : MutableList<Location>

    fun getMyLocations() : List<Location> {
        return myLocations
    }

    fun setMyLocations(myLocations : MutableList<Location>){
        this.myLocations = myLocations
    }

    companion object {
        private lateinit var singleton: MyApplication

        fun getInstance(): MyApplication {
            return singleton
        }
    }


    override fun onCreate() {
        super.onCreate()
        singleton = this
        myLocations = arrayListOf()
    }
}