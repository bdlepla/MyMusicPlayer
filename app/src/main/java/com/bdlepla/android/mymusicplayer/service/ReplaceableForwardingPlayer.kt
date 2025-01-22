package com.bdlepla.android.mymusicplayer.service

import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

/**
 * A [Player] implementation that delegates to an actual [Player] implementation that is
 * replaceable by another instance by calling [setPlayer].
 */
class ReplaceableForwardingPlayer(private var player: Player) : ForwardingSimpleBasePlayer(player) {

    private val listeners: MutableList<Player.Listener> = arrayListOf()
    // After disconnecting from the Cast device, the timeline of the CastPlayer is empty, so we
    // need to track the playlist to be able to transfer the playlist back to the local player after
    // having disconnected.
    private val playlist: MutableList<MediaItem> = arrayListOf()
    private var currentMediaItemIndex: Int = 0

    private val playerListener: Player.Listener = PlayerListener()

    init { player.addListener(playerListener) }

    /** Sets a new [Player] instance to which the state of the previous player is transferred. */
    fun setPlayer(player: Player) {
        // Remove add all listeners before changing the player state.
        for (listener in listeners) {
            this.player.removeListener(listener)
            player.addListener(listener)
        }
        // Add/remove our listener we use to workaround the missing metadata support of CastPlayer.
        this.player.removeListener(playerListener)
        player.addListener(playerListener)

        player.repeatMode = this.player.repeatMode
        player.shuffleModeEnabled = this.player.shuffleModeEnabled
        player.playlistMetadata = this.player.playlistMetadata
        player.trackSelectionParameters = this.player.trackSelectionParameters
        player.volume = this.player.volume
        player.playWhenReady = this.player.playWhenReady

        // Prepare the new player.
        player.setMediaItems(playlist.take(1), 0, 0)
        //player.setMediaItems(playlist, currentMediaItemIndex, this.player.contentPosition)
        // The above line sends too much info for the network message to handle (> 256K)
        // So break up the items into chunks of 50(?) then set the current item and position
        // to play after sending the items.
//        player.clearMediaItems()
//        val chunkSize = 50
//        val chunksOfMediaItems = playlist.chunked(chunkSize)
//        chunksOfMediaItems.forEachIndexed{ idx, items ->
//            player.replaceMediaItems(idx*chunkSize, (idx+1)*chunkSize-1, items)
//        }
//        player.seekTo(currentMediaItemIndex, this.player.contentPosition)

        player.prepare()

        // Stop the previous player. Don't release so it can be used again.
        this.player.clearMediaItems()
        this.player.stop()

        this.player = player
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(EVENT_MEDIA_ITEM_TRANSITION)
                && !events.contains(EVENT_MEDIA_METADATA_CHANGED)) {
                // CastPlayer does not support onMetaDataChange. We can trigger this here when the
                // media item changes.
                if (playlist.isNotEmpty()) {
                    for (listener in listeners) {
                        listener.onMediaMetadataChanged(
                            playlist[player.currentMediaItemIndex].mediaMetadata
                        )
                    }
                }
            }
            if (events.contains(EVENT_POSITION_DISCONTINUITY)
                || events.contains(EVENT_MEDIA_ITEM_TRANSITION)
                || events.contains(EVENT_TIMELINE_CHANGED)) {
                if (!player.currentTimeline.isEmpty) {
                    currentMediaItemIndex = player.currentMediaItemIndex
                }
            }
        }
    }
}