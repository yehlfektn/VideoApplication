package com.example.videoapplication.network

import android.annotation.SuppressLint
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class TrustAllVerifier : HostnameVerifier {
    @SuppressLint("BadHostnameVerifier")
    override fun verify(p0: String?, p1: SSLSession?): Boolean {
        return true
    }
}