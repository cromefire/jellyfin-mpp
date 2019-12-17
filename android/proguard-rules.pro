-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class org.jellyfin.mpp.**$$serializer { *; }
-keepclassmembers class org.jellyfin.mpp.** {
    *** Companion;
}
-keepclasseswithmembers class org.jellyfin.mpp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
