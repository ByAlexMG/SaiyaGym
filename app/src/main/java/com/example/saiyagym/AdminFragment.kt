package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class AdminFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflar el diseño del fragmento
        val rootView = inflater.inflate(R.layout.fragment_admin, container, false)

        // Obtener la referencia de la WebView desde el layout
        val webView: WebView = rootView.findViewById(R.id.webView)

        // Configurar el cliente de WebView para cargar en la misma WebView
        webView.webViewClient = WebViewClient()

        // Habilitar la configuración de JavaScript para la WebView (si es necesario)
        webView.settings.javaScriptEnabled = true

        // Cargar la URL de la tabla de usuarios de Firebase Authentication
        webView.loadUrl("https://console.firebase.google.com/project/saiyagym-9000/authentication/users?hl=es")

        return rootView
    }
}
