/*
 * Copyright (C) 2018 Andreas Stoffel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.astoffel.laz.ui;

import de.astoffel.laz.model.DataModel;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javafx.beans.property.ObjectPropertyBase;

/**
 *
 * @author astoffel
 */
final class LiveObjectProperty<O, T> extends ObjectPropertyBase<T> {

	private final DataModel model;
	private final O object;
	private final Function<O, T> getter;
	private final BiConsumer<O, T> setter;

	public LiveObjectProperty(DataModel model, O object,
			Function<O, T> getter, BiConsumer<O, T> setter) {
		this(getter.apply(object), model, object, getter, setter);
	}

	private LiveObjectProperty(T initialValue, DataModel model, O object,
			Function<O, T> getter, BiConsumer<O, T> setter) {
		super(initialValue);		
		this.model = model;
		this.object = object;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public void set(T newValue) {
		super.set(newValue);
		try {
			setter.accept(object, newValue);
			model.atomic(session -> {
				session.update(object);
			});
		} catch (Throwable th) {
			try {
				model.atomic(session -> {
					session.refresh(object);
				});
				super.set(getter.apply(object));
			} catch (Throwable inner) {
				th.addSuppressed(inner);
			}
			throw th;
		}
	}

}
