package com.bdlepla.android.mymusicplayer.service

import androidx.media3.cast.CastPlayer
import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import android.os.Bundle
import com.google.common.util.concurrent.ListenableFuture

/**
 * A [Player] implementation that delegates to an actual [Player] implementation that is
 * replaceable by another instance by calling [setNewPlayer].
 */
class ReplaceableForwardingPlayer(private var player: Player) : ForwardingSimpleBasePlayer(player) {

    private val listeners: MutableList<Player.Listener> = arrayListOf()
    // After disconnecting from the Cast device, the timeline of the CastPlayer is empty, so we
    // need to track the playlist to be able to transfer the playlist back to the local player after
    // having disconnected.
    private val playlist: MutableList<MediaItem> = arrayListOf()
    private var currentMediaItemIndex: Int = 0

    private val playerListener: Player.Listener = PlayerListener()

    init {
        player.addListener(playerListener)
        updatePlaylist()
    }

    private fun updatePlaylist() {
        playlist.clear()
        for (i in 0 until player.mediaItemCount) {
            val item = player.getMediaItemAt(i)
            val fullItem = MediaItemTree.getItem(item.mediaId) ?: item
            playlist.add(fullItem)
        }
        currentMediaItemIndex = player.currentMediaItemIndex
    }

    override fun handleSetMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<*> {
        var finalStartIndex = startIndex
        val castSafeItems = if (player is CastPlayer && mediaItems.size > 100) {
            // Truncate for Cast if too large to avoid Binder transaction failures.
            val end = (startIndex + 100).coerceAtMost(mediaItems.size)
            val start = (end - 100).coerceAtLeast(0)
            finalStartIndex -= start
            mediaItems.subList(start, end).maybeStripExtras()
        } else {
            mediaItems.maybeStripExtras()
        }
        return super.handleSetMediaItems(castSafeItems, finalStartIndex, startPositionMs)
    }

    override fun handleAddMediaItems(index: Int, mediaItems: List<MediaItem>): ListenableFuture<*> {
        return super.handleAddMediaItems(index, mediaItems.maybeStripExtras())
    }

    private fun List<MediaItem>.maybeStripExtras(): List<MediaItem> {
        return if (player is CastPlayer) {
            this.map { it.toCastSafeItem() }
        } else {
            this
        }
    }

    private fun MediaItem.toCastSafeItem(): MediaItem {
        val ipAddress = LocalHttpServer.getLocalIpAddress()
        val castUri = if (ipAddress != null && this.localConfiguration?.uri != null) {
            val filePath = this.localConfiguration!!.uri.path
            "http://$ipAddress:8080$filePath"
        } else {
            this.localConfiguration?.uri.toString()
        }

        return this.buildUpon()
            .setUri(castUri)
            .setMediaMetadata(this.mediaMetadata.toCastSafeMetadata())
            .setRequestMetadata(MediaItem.RequestMetadata.EMPTY)
            .build()
    }

    private fun MediaMetadata.toCastSafeMetadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(this.title)
            .setArtist(this.artist)
            .setAlbumTitle(this.albumTitle)
            .build()
    }

    override fun handleSetPlaylistMetadata(playlistMetadata: MediaMetadata): ListenableFuture<*> {
        return super.handleSetPlaylistMetadata(
            if (player is CastPlayer) playlistMetadata.toCastSafeMetadata() else playlistMetadata
        )
    }

    override fun handleRelease(): ListenableFuture<*> {
        player.removeListener(playerListener)
        return super.handleRelease()
    }

    /** Sets a new [Player] instance to which the state of the previous player is transferred. */
    fun setNewPlayer(newPlayer: Player) {
        if (this.player === newPlayer) return

        val previousPlayer = this.player
        val playbackPositionMs = previousPlayer.currentPosition
        val playWhenReady = previousPlayer.playWhenReady

        // Remove listener from old player and add to new one.
        previousPlayer.removeListener(playerListener)
        newPlayer.addListener(playerListener)

        // Transfer state.
        newPlayer.repeatMode = previousPlayer.repeatMode
        newPlayer.shuffleModeEnabled = previousPlayer.shuffleModeEnabled
        newPlayer.playlistMetadata = if (newPlayer is CastPlayer) {
            previousPlayer.playlistMetadata.toCastSafeMetadata()
        } else {
            previousPlayer.playlistMetadata
        }
        newPlayer.trackSelectionParameters = previousPlayer.trackSelectionParameters
        newPlayer.volume = previousPlayer.volume

        // Prepare the new player with the current playlist.
        if (playlist.isNotEmpty()) {
            var castSafePlaylist = if (newPlayer is CastPlayer) {
                playlist.map { it.toCastSafeItem() }
            } else {
                playlist
            }
            
            // If still too large for Cast, we need to truncate to avoid Binder transaction failures.
            // 100 is a safe limit for most devices.
            var startIndex = currentMediaItemIndex
            if (newPlayer is CastPlayer && castSafePlaylist.size > 100) {
                val end = (startIndex + 100).coerceAtMost(castSafePlaylist.size)
                val start = (end - 100).coerceAtLeast(0)
                startIndex -= start
                castSafePlaylist = castSafePlaylist.subList(start, end)
            }
            
            newPlayer.setMediaItems(castSafePlaylist, startIndex, playbackPositionMs)
        }
        newPlayer.playWhenReady = playWhenReady
        newPlayer.prepare()

        // Stop the previous player.
        previousPlayer.stop()
        previousPlayer.clearMediaItems()

        this.player = newPlayer
        setPlayer(newPlayer)
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
                && !events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                // CastPlayer does not support onMetaDataChange. We can trigger this here when the
                // media item changes.
                if (playlist.isNotEmpty()) {
                    val index = player.currentMediaItemIndex
                    if (index < playlist.size) {
                        val metadata = playlist[index].mediaMetadata
                        // We can't easily notify external listeners from here without a reference to them,
                        // but ForwardingSimpleBasePlayer should handle metadata changes if the delegate notifies.
                    }
                }
            }
            if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)
                || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
                || events.contains(Player.EVENT_TIMELINE_CHANGED)) {
                updatePlaylist()
            }
        }
    }
}