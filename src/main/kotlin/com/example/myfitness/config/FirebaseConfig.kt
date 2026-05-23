package com.example.myfitness.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun initFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount = ClassPathResource("firebase-service-account.json").inputStream
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)
        }
    }
}
