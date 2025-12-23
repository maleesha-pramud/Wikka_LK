package com.wigerlabs.wikka_lk.config;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig(){
        packages("com.wigerlabs.wikka_lk.controller");
        packages("com.wigerlabs.wikka_lk.middleware");
        register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    }
}