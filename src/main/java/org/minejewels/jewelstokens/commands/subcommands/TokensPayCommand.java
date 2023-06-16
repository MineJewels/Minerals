package org.minejewels.jewelstokens.commands.subcommands;

import net.abyssdev.abysslib.command.AbyssSubCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.utils.Utils;
import org.bukkit.entity.Player;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.minejewels.jewelstokens.JewelsTokens;
import org.minejewels.jewelstokens.player.TokenPlayer;

public class TokensPayCommand extends AbyssSubCommand<JewelsTokens> {

    public TokensPayCommand(final JewelsTokens plugin) {
        super(plugin, 2, Sets.immutable.of("pay", "send"));
    }

    @Override
    public void execute(CommandContext<?> context) {

        final Player player = context.getSender();

        if (context.getArguments().length != 2) {
            this.plugin.getMessageCache().sendMessage(player, "messages.help-player");
            return;
        }

        final Player target = context.asPlayer(0);

        if (target == null) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-player");
            return;
        }

        if (!Utils.isLong(context.asString(1))) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-number");
            return;
        }

        final long amount = context.asLong(1);

        if (amount <= 0) {
            this.plugin.getMessageCache().sendMessage(player, "messages.invalid-number");
            return;
        }

        final TokenPlayer tokenPlayer = this.plugin.getPlayerStorage().get(player.getUniqueId());

        if (!tokenPlayer.hasEnoughTokens(amount)) {
            this.plugin.getMessageCache().sendMessage(player, "messages.not-enough-tokens", new PlaceholderReplacer().addPlaceholder("%amount%", Utils.format(tokenPlayer.getTokensNeeded(amount))));
            return;
        }

        final TokenPlayer tokenTarget = this.plugin.getPlayerStorage().get(target.getUniqueId());

        tokenPlayer.removeTokens(amount);
        tokenTarget.addTokens(amount);

        final PlaceholderReplacer replacer = new PlaceholderReplacer()
                .addPlaceholder("%amount%", Utils.format(amount));

        this.plugin.getMessageCache().sendMessage(player, "messages.tokens-sent", replacer.addPlaceholder("%target%", target.getName()));
        this.plugin.getMessageCache().sendMessage(target, "messages.tokens-received", replacer.addPlaceholder("%player%", player.getName()));
    }
}
