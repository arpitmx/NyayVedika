package com.ncs.nyayvedika.UI.Chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ncs.nyayvedika.Domain.Api.ChatApiService
import com.ncs.nyayvedika.Domain.Api.RetrofitClient
import com.ncs.nyayvedika.Domain.Models.Answer
import com.ncs.o2.Constants.NotificationType
import com.ncs.o2.Domain.Models.ServerResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class ChatViewModel : ViewModel() {

    private val chatsApi by lazy {
        RetrofitClient.getInstance().create(ChatApiService::class.java)
    }


    private val _answerLiveData =  MutableLiveData<ServerResult<Answer?>>()
    val answerLiveData: LiveData<ServerResult<Answer?>> get() = _answerLiveData

    companion object{
        private const val TAG : String = "ChatViewModel"
    }

    private fun getJsonPayload(str:String): JsonObject {
        return Gson().fromJson(str, JsonObject::class.java)
    }

    suspend fun getAnswer(question : String) {

        viewModelScope.launch{

            try {

                _answerLiveData.postValue(ServerResult.Progress)
                val response : Response<Answer> = chatsApi.getAnswer(question)
                if (response.isSuccessful){

                    _answerLiveData.postValue(ServerResult.Success(response.body()))
                    Timber.d("$TAG : Server result success: ${response.body()?.answer} ")

                }else{
                    _answerLiveData.postValue(ServerResult.Failure("Code ${response.code()} : ${response.message()}"))
                    Timber.d("$TAG : Server result Not success: ${response.body()?.answer} ")
                }

            } catch (e: UnknownHostException){

                Timber.tag(TAG).d("Unknow host : ${e.message}")
                _answerLiveData.postValue(ServerResult.Failure(e.message.toString()))

            } catch (e: ConnectException){

                Timber.tag(TAG).d("Retrying Connection : ${e.message}")
                _answerLiveData.postValue(ServerResult.Failure(e.message.toString()))

            } catch (e:TimeoutException){

                Timber.tag(TAG).d("Connection timeout : ${e.message}")
                _answerLiveData.postValue(ServerResult.Failure(e.message.toString()))


            } catch (e:Exception){

                Timber.tag(TAG).d("Failed. : ${e.message}")
                _answerLiveData.postValue(ServerResult.Failure(e.message.toString()))
            }
        }

    }

}