package com.dogpaws.frontend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

/**
 * Created on 2025-02-05 by 최윤서
 */

 @Configuration
public class FirebaseConfig {

     @Value("${firebase.database.url}")
    private String firebaseUrl;

     @Value("${firebase.config.path}")
    private String firebaseConfigPath;

     @PostConstruct
     public void init() {
         try{
             FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);

             FirebaseOptions options = FirebaseOptions.builder()
                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                     .setDatabaseUrl(firebaseUrl).build();

             if(FirebaseApp.getApps().isEmpty()){
                 FirebaseApp.initializeApp(options);
                 System.out.println("FirebaseApp 초기화");
             }

         } catch (Exception e) {
             e.printStackTrace();
             System.out.println("Firebase 초기화 실패 : "+ e.getMessage());
         }
     }
}
