package org.jellyfin.mpp.common

import kotlinx.serialization.Serializable

@Serializable
data class JList<T>(val Items: List<T>)

@Serializable
data class JView(
    val Name: String,
    val ServerId: String,
    val Id: String,
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
    val Type: ViewType,
    val People: List<String>,
    val Studios: List<String>,
    val GenreItems: List<String>,
    val LocalTrailerCount: Int,
    val UserData: UserData,
    val ChildCount: Int,
    val SpecialFeatureCount: Int,
    val DisplayPreferencesId: String,
    val Tags: List<String>,
    val PrimaryImageAspectRatio: Int,
    val CollectionType: CollectionType,
    val ImageTags: Map<ImageType, String>,
    val BackdropImageTags: List<String>,
    val ScreenshotImageTags: List<String>,
    val LocationType: LocationType,
    val LockedFields: List<String>,
    val LockData: Boolean
)

enum class Access {
    Full
}

enum class ViewType {
    CollectionFolder
}

@Serializable
data class UserData(
    val PlaybackPositionTicks: Int,
    val PlayCount: Int,
    val IsFavorite: Boolean,
    val Played: Boolean,
    val Key: String
)

enum class CollectionType {
    boxsets,
    movies,
    homevideos
}

enum class ImageType {
    Primary
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
