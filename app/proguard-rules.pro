-keep public class com.rootix.launcher.commands.main.raw.** { *; }
-keep public class com.rootix.launcher.commands.main.specific.** { *; }
-keep public class com.rootix.launcher.commands.tuixt.raw.** { *; }
-keep public class com.rootix.launcher.tuils.GenericFileProvider { *; }
-keep public class com.rootix.launcher.tuils.PrivateIOReceiver { *; }
-keep public class com.rootix.launcher.tuils.PublicIOReceiver { *; }
-keep class com.rootix.launcher.managers.** { *; }
-keep class com.rootix.launcher.tuils.libsuperuser.**
-keep class com.rootix.launcher.managers.suggestions.HideSuggestionViewValues
-keep public class it.andreuzzi.comparestring2.**

-dontwarn com.rootix.launcher.commands.main.raw.**

-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn org.htmlcleaner.**
-dontwarn com.jayway.jsonpath.**
-dontwarn org.slf4j.**

-dontwarn org.jdom2.**