package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.managed.CommandTemplate
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.utils.Context
import xyz.gnarbot.gnar.utils.ln

@Command(
        aliases = arrayOf("selfroles", "selfrole"),
        usage = "(add|remove|clear) [@role]",
        description = "Set self-roles that users can assign to themselves.",
        category = Category.MODERATION,
        permissions = arrayOf(Permission.MANAGE_SERVER)
)
class SelfRoleCommand : CommandTemplate() {
    @Executor(0, description = "Add a self-role.")
    fun add(context: Context, role: Role) {
        if (role == context.guild.publicRole) {
            context.send().error("You can't add the public role!").queue()
            return
        }

        if (!context.guild.selfMember.canInteract(role)) {
            context.send().error("That role is higher than my role! Fix by changing the role hierarchy.").queue()
            return
        }

        if (role.id in context.guildOptions.selfRoles) {
            context.send().error("${role.asMention} is already added as a self-assignable role.").queue()
            return
        }

        context.guildOptions.selfRoles.add(role.id)
        context.guildOptions.save()

        context.send().embed("Self-Roles") {
            desc {
                "Added ${role.asMention} to the list of self-assignable roles."
            }
        }.action().queue()
    }

    @Executor(1, description = "Remove a self-role.")
    fun remove(context: Context, role: Role) {
        if (role.id !in context.guildOptions.selfRoles) {
            context.send().error("${role.asMention} is not a self-assignable role.").queue()
            return
        }

        context.guildOptions.selfRoles.remove(role.id)
        context.guildOptions.save()

        context.send().embed("Self-Roles") {
            desc {
                "Removed ${role.asMention} from the list of self-assignable roles."
            }
        }.action().queue()
    }

    @Executor(2, description = "Clear all self-assignable roles.")
    fun clear(context: Context) {
        if (context.guildOptions.selfRoles.isEmpty()) {
            context.send().error("This guild doesn't have any self-assignable roles.").queue()
            return
        }

        context.guildOptions.selfRoles.clear()
        context.guildOptions.save()

        context.send().embed("Self-Roles") {
            desc {
                "Cleared the list of self-assignable roles."
            }
        }.action().queue()
    }

    @Executor(3, description = "List self-assignable roles.")
    fun list(context: Context) {
        context.send().embed("Self-Roles") {
            desc {
                if (context.guildOptions.selfRoles.isEmpty()) {
                    "This guild doesn't have any self-assignable roles."
                } else {
                    buildString {
                        context.guildOptions.selfRoles.map(context.guild::getRoleById)
                                .filterNotNull()
                                .map(IMentionable::getAsMention)
                                .forEach { append("• ").append(it).ln() }
                    }
                }
            }
        }.action().queue()
    }
}