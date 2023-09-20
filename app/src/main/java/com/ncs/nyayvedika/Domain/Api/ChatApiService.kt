package com.ncs.nyayvedika.Domain.Api

import com.ncs.nyayvedika.Domain.Models.Answer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/*
File : VedikaApi.kt -> com.ncs.nyayvedika.Domain.Interfaces
Description : Api Interface for Vedika Backend 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 11:52 pm on 17/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 

*/

interface ChatApiService {
    @GET("/chat")
    suspend fun getAnswer(@Query("input") question: String) : Response<Answer>
}