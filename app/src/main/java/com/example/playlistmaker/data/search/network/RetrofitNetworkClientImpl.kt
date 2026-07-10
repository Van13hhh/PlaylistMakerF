package com.example.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RetrofitNetworkClientImpl(
    private val context: Context,
    private val itunesService: ItunesApiService
) : RetrofitNetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TrackSearchRequest) return Response().apply { resultCode = 400 }

        return try {
            val response = itunesService.search(dto.expression).execute()
            val body = response.body()

            if (body != null) {
                body.apply { resultCode = response.code() }
            } else {
                Response().apply { resultCode = response.code() }
            }
        } catch (e: SocketTimeoutException) {
            // Таймаут соединения
            Response().apply { resultCode = -1 }
        } catch (e: UnknownHostException) {
            // Нет интернета (DNS не разрешился)
            Response().apply { resultCode = -1 }
        } catch (e: Exception) {
            // Любая другая сетевая ошибка
            Response().apply { resultCode = -1 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}