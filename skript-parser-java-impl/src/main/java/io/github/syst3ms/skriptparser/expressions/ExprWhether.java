package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Main;
import io.github.syst3ms.skriptparser.event.Event;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.parsing.ParseResult;
import io.github.syst3ms.skriptparser.parsing.SkriptParser;
import io.github.syst3ms.skriptparser.parsing.SyntaxParser;
import io.github.syst3ms.skriptparser.pattern.CompoundElement;
import io.github.syst3ms.skriptparser.pattern.ExpressionElemen;
import io.github.syst3ms.skriptparser.pattern.TextElement;

import java.util.Collections;

public class ExprWhether implements Expression<Boolean> {
	private Expression<Boolean> condition;

	static {
	    Main.getMainRegistration().addExpression(
	            ExprWhether.class,
                Boolean.class,
                true,
                "whether %~boolean%"
        );
		SkriptParser.setWhetherPattern(
			new CompoundElement(
				new TextElement("whether "),
				new ExpressionElemen(Collections.singletonList(SyntaxParser.BOOLEAN_PATTERN_TYPE), ExpressionElemen.Acceptance.EXPRESSIONS_ONLY, false)
			)
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, ParseResult parseResult) {
		condition = (Expression<Boolean>) expressions[0];
		return true;
	}

	@Override
	public Boolean[] getValues(Event e) {
		return condition.getValues(e);
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "whether " + condition.toString(e, debug);
	}
}
