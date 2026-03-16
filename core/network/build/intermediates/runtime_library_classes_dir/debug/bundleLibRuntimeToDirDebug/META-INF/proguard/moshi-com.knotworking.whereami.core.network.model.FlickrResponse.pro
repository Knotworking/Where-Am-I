-keepnames class com.knotworking.whereami.core.network.model.FlickrResponse
-if class com.knotworking.whereami.core.network.model.FlickrResponse
-keep class com.knotworking.whereami.core.network.model.FlickrResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
