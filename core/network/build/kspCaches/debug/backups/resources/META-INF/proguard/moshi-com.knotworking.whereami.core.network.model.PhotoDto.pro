-keepnames class com.knotworking.whereami.core.network.model.PhotoDto
-if class com.knotworking.whereami.core.network.model.PhotoDto
-keep class com.knotworking.whereami.core.network.model.PhotoDtoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
