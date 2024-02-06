/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package io.github.syst3ms.skriptparser.expressions.arithmetic;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class ArithmeticExpressionInfo<T> implements ArithmeticGettable<T> {
	
	private final Expression<? extends T> expression;
	
	public ArithmeticExpressionInfo(Expression<? extends T> expression) {
		this.expression = expression;
	}

	@Override
	public T get(TriggerContext ctx) {
		T object = expression.getSingle(ctx).orElse(null);
		return object == null ? Arithmetics.getDefaultValue(expression.getReturnType()) : object;
	}

	@Override
	public Class<? extends T> getReturnType() {
		return expression.getReturnType();
	}

}
