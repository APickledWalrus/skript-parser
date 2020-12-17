package io.github.syst3ms.skriptparser.effects;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.*;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.sections.SecLoop;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Skips the current looped value and continues to the next one in the list, if it exists.
 *
 * @name Continue
 * @pattern continue
 * @since ALPHA
 * @author Mwexim
 */
public class EffContinue extends Effect {

    static {
        Parser.getMainRegistration().addEffect(
            EffContinue.class,
            4,
            "continue"
        );
    }

    private SecLoop loop;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        List<SecLoop> loops = new ArrayList<>();
        for (CodeSection sec : parseContext.getParserState().getCurrentSections()) {
            if (sec instanceof SecLoop) {
                loops.add((SecLoop) sec);
            }
        }
        if (loops.size() == 0) {
            parseContext.getLogger().error("You can only use the 'continue' in a loop!", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        // Closest loop will be the first item
        loop = loops.get(0);
        assert loop != null;

        return true;
    }

    @Override
    public void execute(TriggerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Optional<? extends Statement> walk(TriggerContext ctx) {
        loop.walk(ctx, true);
        return Optional.empty();
    }

    @Override
    public String toString(@Nullable TriggerContext ctx, boolean debug) {
        return "continue";
    }
}
