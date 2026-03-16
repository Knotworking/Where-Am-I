-keepnames class com.knotworking.whereami.core.network.model.PhotosResponse
-if class com.knotworking.whereami.core.network.model.PhotosResponse
-keep class com.knotworking.whereami.core.network.model.PhotosResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
