-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep public interface com.streethawk.library.push.ISHObserver{
    public *;
}
-keep public class com.streethawk.library.push.GCMIntentService{
    public *;
}
-keep public class com.streethawk.library.push.GCMReceiver{
    public *;
}
-keep public class com.streethawk.library.push.Push{
    public *;
}
-keep public class com.streethawk.library.push.PushDataForApplication{
    public *;
}
-keep public class com.streethawk.library.push.PushNotificationBroadcastReceiver{
    public *;
}
-keep public class com.streethawk.library.push.SHCoreModuleReceiver{
    public *;
}
-keep public class com.streethawk.library.push.SHFeedbackActivity{
    public *;
}
-keep public class com.streethawk.library.push.PushNotificationBroadcastReceiver{
    public *;
}