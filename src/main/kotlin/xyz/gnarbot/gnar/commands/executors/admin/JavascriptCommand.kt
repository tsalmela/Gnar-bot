package xyz.gnarbot.gnar.commands.executors.admin

import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.utils.Context
import javax.script.*

@Command(
        aliases = arrayOf("js"),
        description = "Run JS scripts.",
        admin = true,
        category = Category.NONE
)
class JavascriptCommand : CommandExecutor() {
    companion object {
        val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("javascript")
    }

    override fun execute(context: Context, args: Array<String>) {
        val script = args.joinToString(" ")
        if (script.isNullOrEmpty()) {
            context.send().error("Script can not be empty.").queue()
            return
        }

        val scope = SimpleScriptContext()

        scope.getBindings(ScriptContext.ENGINE_SCOPE).put("context", context)

        val result = try {
            scriptEngine.eval(script, scope)
        } catch (e: ScriptException) {
            return context.send().exception(e).queue()
        }

        context.send().text(result.toString()).queue()
    }

}