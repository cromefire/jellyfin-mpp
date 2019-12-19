package org.jellyfin.mpp.common

import kotlinx.serialization.*

@Serializable
data class JList<T>(val Items: List<T>)

@Serializable
sealed class JView {
    abstract val Name: String
    abstract val ServerId: String
    abstract val Id: String
    abstract val UserData: UserData
    abstract val ImageTags: Map<ImageType, String>

    @Serializable
    @SerialName("CollectionFolder")
    class CollectionFolder(
        override val Name: String,
        override val ServerId: String,
        override val Id: String,
        val Etag: String,
        val DateCreated: String,
        val CanDelete: Boolean,
        val CanDownload: Boolean,
        val SortName: String,
        val ExternalUrls: List<String>,
        val Path: String,
        val EnableMediaSourceDisplay: Boolean,
        val Taglines: List<String>,
        val Genres: List<String>,
        val PlayAccess: Access,
        val RemoteTrailers: List<String>,
        val ProviderIds: Map<String, String>,
        val IsFolder: Boolean,
        val ParentId: String,
        val People: List<String>,
        val Studios: List<String>,
        val GenreItems: List<String>,
        val LocalTrailerCount: Int,
        override val UserData: UserData,
        val ChildCount: Int,
        val SpecialFeatureCount: Int,
        val DisplayPreferencesId: String,
        val PrimaryImageAspectRatio: Double? = null,
        val CollectionType: CollectionType,
        override val ImageTags: Map<ImageType, String>,
        val BackdropImageTags: List<String>,
        val ScreenshotImageTags: List<String>,
        val LocationType: LocationType,
        val LockedFields: List<String>,
        val LockData: Boolean
    ) : JView()

    @Serializable
    @SerialName("Movie")
    data class Movie(
        override val Name: String,
        override val ServerId: String,
        override val Id: String,
        val HasSubtitles: Boolean = false,
        val Container: String,
        val PremiereDate: String,
        val CriticRating: Int? = null,
        val OfficialRating: String? = null,
        val CommunityRating: Float? = null,
        val RunTimeTicks: String,
        val ProductionYear: Int,
        val IsFolder: Boolean,
        override val UserData: UserData,
        val PrimaryImageAspectRatio: Double? = null,
        val VideoType: VideoType,
        override val ImageTags: Map<ImageType, String>,
        val BackdropImageTags: List<String>,
        val LocationType: LocationType,
        val MediaType: MediaType
    ) : JView()

    @Serializable
    @SerialName("Audio")
    data class MusicTrack(
        override val Name: String,
        override val ServerId: String,
        override val Id: String,
        override val UserData: UserData,
        override val ImageTags: Map<ImageType, String>
    ) : JView()
}

enum class Access {
    Full
}

enum class ViewType {
    CollectionFolder, Movie
}

@Serializable
data class UserData(
    val PlayedPercentage: Double = 0.toDouble(),
    val PlaybackPositionTicks: String,
    val PlayCount: Int,
    val IsFavorite: Boolean,
    val LastPlayedDate: String? = null,
    val Played: Boolean,
    val Key: String
)

enum class CollectionType {
    boxsets,
    movies,
    homevideos,
    tvshows
}

enum class ImageType {
    Primary,
    Backdrop,
    Thumb
}

enum class VideoType {
    VideoFile
}

enum class MediaType {
    Video, Audio
}

enum class LocationType {
    FileSystem
}

@Serializable
data class User(
    val Name: String,
    val ServerId: String,
    val Id: String,
    val HasPassword: Boolean,
    val LastLoginDate: String,
    val LastActivityDate: String,
    val Configuration: UserConfiguration,
    val Policy: UserPolicy
)

@Serializable
data class UserConfiguration(
    val EnableNextEpisodeAutoPlay: Boolean
)

@Serializable
data class UserPolicy(
    val IsAdministrator: Boolean,
    val IsHidden: Boolean,
    val IsDisabled: Boolean
)

@Serializable
data class AuthenticateResponse(
    val ServerId: String,
    val AccessToken: String,
    val User: User
)

@Serializable
data class AuthenticateRequest(
    val Username: String,
    val Pw: String
)
