package xyz.gnarbot.gnar.commands.executors.music

import com.jagrosh.jdautilities.menu.PaginatorBuilder
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.TrackContext
import xyz.gnarbot.gnar.utils.Utils

@Command(
        aliases = arrayOf("queue", "list"),
        description = "Shows the music that's currently queued.",
        category = Category.MUSIC
)
class QueueCommand : CommandExecutor() {
    override fun execute(context: Context, args: Array<String>) {
        val manager = Bot.getPlayers().getExisting(context.guild)
        if (manager == null) {
            context.send().error("There's no music player in this guild.\n" +
                    "\uD83C\uDFB6` _play (song/url)` in a voice channel to start playing some music!").queue()
            return
        }

        val queue = manager.scheduler.queue
        var queueLength = 0L

        PaginatorBuilder(Bot.getWaiter())
                .setTitle("Music Queue")
                .setColor(context.guild.selfMember.color)
                .apply {
                    if (queue.isEmpty()) {
                        add("**Empty queue.** Add some music with `_play url|YT search`.")
                    } else for (track in queue) {
                        add("`[${Utils.getTimestamp(track.duration)}]` __[${track.info.title}](${track.info.uri})__ " +
                                track.getUserData(TrackContext::class.java).requester.asMention)
                        queueLength += track.duration
                    }
                }
                .field("Now Playing", false) {
                    val track = manager.player.playingTrack
                    if (track == null) {
                        "Nothing"
                    } else {
                        "**[${track.info.title}](${track.info.uri})**"
                    }
                }
                .field("Entries", true, queue.size)
                .field("Total Duration", true, Utils.getTimestamp(queueLength))
                .field("Repeating", true, manager.scheduler.repeatOption)
                .build().display(context.channel)
    }
}
