package de.uni_tuebingen.fsi.eduroam

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bSave.setOnClickListener {
            val username = "${tietUsername.text}@uni-tuebingen.de"
            val password = tietPassword.text.toString()
            addEduroamProfile(username, password);
        }
    }

    fun addEduroamProfile(username: String, password: String) {
        val wifiConfig = getWifiConfig()
        wifiConfig.enterpriseConfig = getEapConfig(username, password)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val netId = wifiManager.addNetwork(wifiConfig)
        if (netId != -1)
            wifiManager.enableNetwork(netId, true)
    }

    private fun getWifiConfig(): WifiConfiguration {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "eduroam"
        wifiConfig.allowedProtocols.clear()
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        wifiConfig.allowedKeyManagement.clear()
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
        wifiConfig.allowedAuthAlgorithms.clear()
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        wifiConfig.allowedPairwiseCiphers.clear()
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        wifiConfig.allowedGroupCiphers.clear()
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        return wifiConfig
    }

    private fun getEapConfig(username: String, password: String): WifiEnterpriseConfig {
        val eapConfig = WifiEnterpriseConfig()
        eapConfig.caCertificate = getCertificate()
        eapConfig.altSubjectMatch =
                "DNS:u-002-sias01.uni-tuebingen.de;DNS:u-002-sias02.uni-tuebingen.de"
        eapConfig.eapMethod = WifiEnterpriseConfig.Eap.PEAP
        eapConfig.identity = username
        eapConfig.password = password
        eapConfig.phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
        return eapConfig
    }

    fun getCertificate(): X509Certificate {
        // Extract the certificate
        val certificateStream = resources.openRawResource(R.raw.deutsche_telekom_root_ca_2)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificate =
                certificateFactory.generateCertificate(certificateStream) as X509Certificate

        // TODO: Verify the certificate
        certificate.checkValidity()

        return certificate;
    }
}
