plugins {
    id 'com.android.application'
}

android {
    namespace 'es.chiteroman.bootloaderspoofer'
    compileSdk 35
    buildToolsVersion '35.0.0'

    packagingOptions {
        resources {
            excludes += "**"
        }
    }

    defaultConfig {
        applicationId "es.chiteroman.bootloaderspoofer"
        minSdk 26
        targetSdk 35
        versionCode 40
        versionName '4.0'
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.debug
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly 'de.robv.android.xposed:api:82'
    implementation 'org.bouncycastle:bcpkix-jdk18on:1.78.1'
}
