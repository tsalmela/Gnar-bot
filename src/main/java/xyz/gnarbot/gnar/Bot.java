package xyz.gnarbot.gnar;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.avarel.aje.ast.GlobalVisitorSettings;
import xyz.gnarbot.gnar.commands.CommandRegistry;
import xyz.gnarbot.gnar.db.Database;
import xyz.gnarbot.gnar.db.OptionsRegistry;
import xyz.gnarbot.gnar.listeners.BotListener;
import xyz.gnarbot.gnar.music.PlayerRegistry;
import xyz.gnarbot.gnar.utils.DiscordLogBack;
import xyz.gnarbot.gnar.utils.MyAnimeListAPI;
import xyz.gnarbot.gnar.utils.SimpleLogToSLF4JAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main bot class.
 *
 * @author Avarel, Xevryll
 */
public final class Bot {
    public static final Logger LOG = LoggerFactory.getLogger("Bot");
    public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static final Credentials KEYS = new Credentials(new File("credentials.conf"));
    public static final BotConfiguration CONFIG = new BotConfiguration(new File("bot.conf"));
    public static final Database DATABASE = new Database("bot");

    private static MyAnimeListAPI malAPI = new MyAnimeListAPI(KEYS.getMalUsername(), KEYS.getMalPassword());

    protected static final BotListener botListener = new BotListener();
    protected static final EventWaiter waiter = new EventWaiter();

    private static final CommandRegistry commandRegistry = new CommandRegistry();
    private static final OptionsRegistry optionsRegistry = new OptionsRegistry();
    private static final PlayerRegistry playerRegistry = new PlayerRegistry();

    private static final List<Shard> shards = new ArrayList<>();

    public static LoadState STATE = LoadState.LOADING;

    public static void main(String[] args) {
        SimpleLogToSLF4JAdapter.install();
        DiscordLogBack.enable();

        // AJE settings
        GlobalVisitorSettings.SIZE_LIMIT = 100;
        GlobalVisitorSettings.ITERATION_LIMIT = 100;
        GlobalVisitorSettings.MILLISECONDS_LIMIT = 5;

        LOG.info("Initializing the Discord bot.");

        LOG.info("Name:\t" + CONFIG.getName());
        LOG.info("JDAs:\t" + KEYS.getShards());
        LOG.info("Prefix:\t" + CONFIG.getPrefix());
        LOG.info("Admins:\t" + CONFIG.getAdmins().size());
        LOG.info("JDA:\t\t" + JDAInfo.VERSION);

        STATE = LoadState.COMPLETE;

        for (int i = 0; i < KEYS.getShards(); i++) {
            Shard shard = new Shard(i);
            shards.add(shard);
            shard.build();
        }

        for (Shard shard : shards) {
            shard.getJda().getPresence().setGame(Game.of(String.format(CONFIG.getGame(), shard.getId())));
        }

        LOG.info("The bot is now fully connected to Discord.");
    }

    public static MyAnimeListAPI getMALAPI() {
        return malAPI;
    }

    public static CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public static PlayerRegistry getPlayers() {
        return playerRegistry;
    }

    public static OptionsRegistry getOptions() {
        return optionsRegistry;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static List<Shard> getShards() {
        return shards;
    }


    public static Shard getShard(int id) {
        return shards.get(id);
    }

    public static Shard getShard(JDA jda) {
        return shards.get(jda.getShardInfo() != null ? jda.getShardInfo().getShardId() : 0);
    }

    public static void restart() {
        LOG.info("Restarting the Discord bot shards.");
        for (Shard shard : shards) {
            shard.revive();
        }
        LOG.info("Discord bot shards have now restarted.");
    }
}
