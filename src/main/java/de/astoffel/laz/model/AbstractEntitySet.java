/*
 * Copyright (C) 2018 astoffel
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
package de.astoffel.laz.model;

import de.astoffel.laz.model.transfer.TransferEntity;
import de.astoffel.laz.model.transfer.TransferEntityType;
import de.astoffel.laz.model.transfer.TransferException;
import de.astoffel.laz.model.transfer.TransferModel;
import de.astoffel.laz.model.transfer.TransferSession;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author astoffel
 */
public abstract class AbstractEntitySet<E extends AbstractEntity<T>, T extends TransferEntity> {

	@FunctionalInterface
	static interface FindEntity<T> {

		Optional<T> find(TransferSession session) throws TransferException;
	}

	@FunctionalInterface
	static interface FindEntities<T> {

		List<T> findAll(TransferSession session) throws TransferException;
	}

	private final TransferModel transferModel;
	private final TransferEntityType<T> transferType;
	private final Map<Long, Reference<E>> entities;

	AbstractEntitySet(TransferModel transferModel,
			TransferEntityType<T> transferType) {
		this.transferModel = transferModel;
		this.transferType = transferType;
		this.entities = new WeakHashMap<>();
	}

	final TransferModel transferModel() {
		return transferModel;
	}

	abstract T createTransfer();

	abstract E createEntity(T transfer);

	final E wrap(T transfer) {
		if (transfer == null) {
			return null;
		}
		var ref = entities.get(transfer.getId());
		var result = ref != null ? ref.get() : null;
		if (result == null) {
			result = createEntity(transfer);
			entities.put(transfer.getId(), new WeakReference<>(result));
		}
		return result;
	}

	final Optional<E> wrap(Optional<T> entity) {
		return entity.map(this::wrap);
	}

	final List<E> wrap(List<T> entities) {
		return entities.stream()
				.map(this::wrap)
				.collect(Collectors.toList());
	}

	final Optional<E> doFind(FindEntity<T> find) {
		try {
			var transfer = transferModel.compute(session -> find.find(session));
			return wrap(transfer);
		} catch (TransferException ex) {
			throw new ModelException(
					String.format("Finding %s failed",
							transferType.name()),
					ex);
		}
	}

	final List<E> doFindAll(FindEntities<T> find) {
		try {
			var transfers = transferModel.compute(session -> find.findAll(session));
			return wrap(transfers);
		} catch (TransferException ex) {
			throw new ModelException(
					String.format("Finding %s failed",
							transferType.name()),
					ex);
		}
	}

	final void clear() {
		entities.clear();
	}

	final void reset() {
		for (var ref : entities.values()) {
			var entity = ref.get();
			if (entity != null) {
				entity.reset();
			}
		}
	}

	public final E create() {
		try {
			var transfer = transferModel.compute(session -> {

				var t = createTransfer();
				transferType.entitySet(session).persist(t);
				return t;
			});
			return wrap(transfer);
		} catch (TransferException ex) {
			throw new ModelException(
					String.format(
							"Failed creating entity %s.",
							transferType.name()),
					ex);
		}
	}

	public final void delete(E entity) {
		try {
			transferModel.execute(session -> {
				transferType.entitySet(session).delete(entity.transfer());
			});
			entities.remove(entity.transfer().getId());
		} catch (TransferException ex) {
			throw new ModelException(
					String.format(
							"Failed deleting entity %s.%s.",
							transferType.name(), entity.transfer().getId()),
					ex);
		}
	}

	public abstract List<E> findAll();
}
