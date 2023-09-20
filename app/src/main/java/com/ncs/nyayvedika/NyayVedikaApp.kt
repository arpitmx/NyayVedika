package com.ncs.nyayvedika

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/*
File : Application.kt -> com.ncs.nyayvedika
Description : Application Class for NyayVedika 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 9:22 pm on 15/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*//*
File : Application.kt -> com.ncs.nyayvedika
Description : Application Class for NyayVedika 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity (@Project : NyayVedika Android)

Creation : 9:22 pm on 15/09/23

*/


@HiltAndroidApp
class NyayVedikaApp : Application() {




    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)  {
            Timber.plant(Timber.DebugTree())
        }


    }

}