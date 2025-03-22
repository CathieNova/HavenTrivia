package net.cathienova.haventrivia;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.cathienova.haventrivia.commands.ModCommands;
import net.cathienova.haventrivia.commands.PermissionHandler;
import net.cathienova.haventrivia.commands.TriviaPermissions;
import net.cathienova.haventrivia.config.CommonConfig;
import net.cathienova.haventrivia.listener.ChatListener;
import net.cathienova.haventrivia.reward.RewardManager;
import net.cathienova.haventrivia.trivia.LeaderboardManager;
import net.cathienova.haventrivia.trivia.TriviaManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod(HavenTrivia.MOD_ID)
public class HavenTrivia
{
    public static final String MOD_ID = "haventrivia";
    public static final String MOD_NAME = "HavenTrivia";
    static final ForgeConfigSpec commonSpec;
    public static final CommonConfig c_config;
    public static TriviaManager triviaManager;
    public static RewardManager rewardManager;
    public static LeaderboardManager leaderboardManager;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        c_config = specPair.getLeft();
    }

    public HavenTrivia()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, commonSpec, MOD_NAME + "-Server.toml");
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        triviaManager = new TriviaManager();
        rewardManager = new RewardManager();
        leaderboardManager = new LeaderboardManager();
    }

    public static void Log(String message)
    {
        LogUtils.getLogger().info("["+ MOD_NAME +"] " + message);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        triviaManager.loadQuestions(event.getServer());
        rewardManager.loadRewards(event.getServer());
        triviaManager.startAuto(event.getServer());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ModCommands.register(dispatcher);
    }
}
