package xyz.gnarbot.gnar.commands;

import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.executors.admin.*;
import xyz.gnarbot.gnar.commands.executors.fun.*;
import xyz.gnarbot.gnar.commands.executors.games.GameLookupCommand;
import xyz.gnarbot.gnar.commands.executors.games.OverwatchLookupCommand;
import xyz.gnarbot.gnar.commands.executors.general.*;
import xyz.gnarbot.gnar.commands.executors.media.*;
import xyz.gnarbot.gnar.commands.executors.mod.*;
import xyz.gnarbot.gnar.commands.executors.music.*;
import xyz.gnarbot.gnar.commands.executors.music.dj.ForceSkipCommand;
import xyz.gnarbot.gnar.commands.executors.music.dj.StopCommand;
import xyz.gnarbot.gnar.commands.executors.music.search.PlayCommand;
import xyz.gnarbot.gnar.commands.executors.music.search.SoundcloudCommand;
import xyz.gnarbot.gnar.commands.executors.music.search.YoutubeCommand;
import xyz.gnarbot.gnar.commands.executors.polls.PollCommand;
import xyz.gnarbot.gnar.commands.executors.test.TestCommand;
import xyz.gnarbot.gnar.commands.executors.test.TestEmbedCommand;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A registry storing CommandExecutor entries for the bot.
 */
public class CommandRegistry {

    /** The mapped registry of invoking key to the classes. */
    private final Map<String, CommandExecutor> commandEntryMap = new LinkedHashMap<>();

    public CommandRegistry() {
        register(new HelpCommand());
        register(new InviteBotCommand());
        register(new PingCommand());
        register(new MathCommand());
        register(new RemindMeCommand());
        register(new GoogleCommand());
        register(new VideoCommand());
        register(new UrbanDictionaryCommand());
        register(new UptimeCommand());
        register(new WhoIsCommand());
        register(new GuildInfoCommand());
        register(new BotInfoCommand());
        register(new DonateCommand());
        register(new RedeemCommand());
        register(new IAmCommand());
        //End General Commands

        //Fun Commands
        register(new TrumpCommand());
        register(new ASCIICommand());
        register(new CoinFlipCommand());
        register(new DialogCommand());
        register(new YodaTalkCommand());
        register(new RollCommand());
        register(new PoopCommand());
        register(new EightBallCommand());
        register(new LeetifyCommand());
        register(new LennyCommand());
        //register(new ProgressionCommand());
        //register(new GoogleyEyesCommand());
        //register(new ServersSharedCommand());
        register(new TextToSpeechCommand());
        register(new ReactCommand());
        //register(new TriviaAnswerCommand());
        //register(new TriviaCommand());
        //register(new GraphCommand());

        register(new PandoraBotCommand());
        register(new MemeCommand());
        //End Fun Commands

        //Mod Commands
        register(new ManageCommandsCommand());
        register(new IgnoreCommand());
        register(new PruneCommand());
        register(new AutoRoleCommand());
        register(new SelfRoleCommand());
        register(new MusicSettingsCommand());
        //End Mod Commands

        //Testing Commands
        register(new TestCommand());
        //End Testing Commands

        //Game Commands
        register(new OverwatchLookupCommand());
        register(new GameLookupCommand());
        //End Game Commands

        //Poll Commands
        register(new PollCommand());
        //End Poll Commands

        //Media Commands
        register(new AnimeSearchCommand());
        register(new MangaSearchCommand());
        register(new CatsCommand());
        register(new ExplosmCommand());
        register(new ExplosmRCGCommand());
        register(new XKCDCommand());
        //End Media Commands

        // Administrator commands
//        register(new SaveCommand());
        register(new ShutdownCommand());
        register(new RestartShardsCommand());
        register(new JavascriptCommand());
        register(new GroovyCommand());
        register(new ShardInfoCommand());
        register(new ThrowError());
        register(new GenerateKeyCommand());
        register(new EmoteListCommand());


        // Test Commands
        register(new TestEmbedCommand());
        register(new QuoteCommand());
        register(new TextToBrickCommand());

        //MUSIC COMMAND
        if (Bot.CONFIG.getMusicEnabled()) {
            register(new MusicHelpCommand());
            register(new PlayCommand());
            register(new PauseCommand());
            register(new StopCommand());
            register(new SkipCommand());
            register(new ForceSkipCommand());
            register(new ShuffleCommand());
            register(new NowPlayingCommand());
            register(new QueueCommand());
            register(new RestartCommand());
            register(new RepeatCommand());
            register(new VoteSkipCommand());
            register(new VolumeCommand());
            register(new JumpCommand());
        }

        register(new YoutubeCommand());
        register(new SoundcloudCommand());
    }

    public Map<String, CommandExecutor> getCommandMap() {
        return commandEntryMap;
    }

    private void register(CommandExecutor cmd) {
        Class<? extends CommandExecutor> cls = cmd.getClass();
        if (!cls.isAnnotationPresent(Command.class)) {
            throw new IllegalStateException("@Command annotation not found for class: " + cls.getName());
        }

        for (String alias : cmd.getInfo().aliases()) {
            registerCommand(alias, cmd);
        }
    }

    /**
     * Register the CommandExecutor instance into the registry.
     * @param label Invoking key.
     * @param cmd Command entry.
     */
    private void registerCommand(String label, CommandExecutor cmd) {
        if (commandEntryMap.containsKey(label.toLowerCase())) {
            throw new IllegalStateException("Command " + label + " is already registered.");
        }
        commandEntryMap.put(label.toLowerCase(), cmd);
    }

    /**
     * Unregisters a CommandExecutor.
     *
     * @param label Invoking key.
     */
    public void unregisterCommand(String label) {
        commandEntryMap.remove(label);
    }

    /**
     * Returns the command registry.
     *
     * @return The command registry.
     */
    public Set<CommandExecutor> getEntries() {
        return new LinkedHashSet<>(commandEntryMap.values());
    }

    public CommandExecutor getCommand(String label) {
        return commandEntryMap.get(label);
    }
}
