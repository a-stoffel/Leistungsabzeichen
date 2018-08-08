/*
 * Copyright (C) 2018 andreas
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
package de.astoffel.laz.model.transfer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.dialect.lock.OptimisticEntityLockException;

/**
 *
 * @author andreas
 */
public abstract class AbstractTransferModel implements TransferModel {

	private SessionFactory sessionFactory;

	protected AbstractTransferModel() {
	}

	protected final void open() {
		sessionFactory = createSessionFactory();
	}

	protected abstract SessionFactory createSessionFactory();

	@Override
	public final void close() throws TransferException {
		try {
			sessionFactory.close();
		} catch (Throwable th) {
			throw new TransferException(th);
		}
	}

	@Override
	public final void execute(Work work) throws TransferException {
		compute((session) -> {
			work.execute(session);
			return null;
		});
	}

	@Override
	public final <R> R compute(WorkWithResult<R> work) throws TransferException {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			R result = work.execute(new TransferSession(session));
			transaction.commit();
			return result;
		} catch (OptimisticEntityLockException ex) {
			transaction.rollback();
			throw new TransferOptimisticLockingException(ex);
		} catch (Throwable th) {
			transaction.rollback();
			throw th;
		}
	}

}
