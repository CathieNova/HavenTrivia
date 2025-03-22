package net.cathienova.haventrivia.datagen;

import net.cathienova.haventrivia.HavenTrivia;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModEngLangProvider extends LanguageProvider {
    public ModEngLangProvider(PackOutput output) {
        super(output, HavenTrivia.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Trivia messages
        add("message.haventrivia.trivia.ask", "§5Trivia Question: §2{question}");
        add("message.haventrivia.trivia.correct", "§5{player} §7got it right in §6{time}s§7! Correct answer: §2{answer}");
        add("message.haventrivia.trivia.timeout", "§7No one answered in time. Correct answer: §2{answer}");
        add("message.haventrivia.trivia.reward", "§7You received: §2{reward}");
        add("message.haventrivia.trivia.leaderboard", "§7Trivia Leaderboard:");
        add("message.haventrivia.trivia.score", "§6{player}§7 - §2{score}§7 points");
        add("message.haventrivia.trivia.noscores", "§7No trivia scores recorded yet.");
        add("message.haventrivia.trivia.added", "§7Added §2{points}§7 points to §6{player}");
        add("message.haventrivia.trivia.removed", "§7Removed §2{points}§7 points from §6{player}");
        add("message.haventrivia.trivia.reset.all", "§7Leaderboard has been reset.");
        add("message.haventrivia.trivia.reset.single", "§7Reset score for §6{player}");
        add("message.haventrivia.trivia.notfound", "§cPlayer §4{player}§c not found.");
        add("message.haventrivia.trivia.added.all", "§7Added §2{points}§7 points to all online players.");
        add("message.haventrivia.trivia.removed.all", "§7Removed §2{points}§7 points from all leaderboard entries.");
        add("message.haventrivia.trivia.about.header", "§7Trivia Categories Overview:");
        add("message.haventrivia.trivia.about.entry", "§6{category}§7 - Questions: §2{questions}§7, Rewards: §b{rewards}");

        // Permissions
        add("permission.name.haventrivia.admin", "§7Trivia Control");
        add("permission.desc.haventrivia.admin", "§7Grants access to all trivia management commands");

        // Command messages
        add("command.haventrivia.start", "§7Trivia has been started.");
        add("command.haventrivia.stop", "§7Trivia has been stopped.");
        add("command.haventrivia.timeout", "§7The current trivia question has been timed out.");
        add("command.haventrivia.reload", "§7Trivia questions and rewards reloaded.");
        add("command.haventrivia.ask", "§7Trivia question asked.");
        add("command.haventrivia.disabled", "§7Trivia is disabled (interval is 0).");
    }
}
