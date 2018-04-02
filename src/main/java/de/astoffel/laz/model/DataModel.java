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
package de.astoffel.laz.model;

import java.nio.file.Path;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author astoffel
 */
public final class DataModel implements AutoCloseable {

	@FunctionalInterface
	public static interface AtomicComputeThrowsTx<R, E extends Exception> {

		public R atomicCompute(DataSession session) throws E;
	}

	@FunctionalInterface
	public static interface AtomicComputeTx<R> extends AtomicComputeThrowsTx<R, RuntimeException> {
	}

	@FunctionalInterface
	public static interface AtomicThrowsTx<E extends Exception> {

		public void atomic(DataSession session) throws E;
	}

	@FunctionalInterface
	public static interface AtomicTx extends AtomicThrowsTx<RuntimeException> {

	}

	private final SessionFactory sessionFactory;

	public DataModel() {
		this(Optional.empty());
	}

	public DataModel(Path prefix) {
		this(Optional.of(prefix));
	}

	private DataModel(Optional<Path> prefix) {
		this.sessionFactory = open(prefix);
	}

	private static SessionFactory open(Optional<Path> prefix) {
		BootstrapServiceRegistryBuilder bootstrapRegistryBuilder
				= new BootstrapServiceRegistryBuilder()
						.applyIntegrator(new FlywayIntegrator());
		StandardServiceRegistryBuilder registryBuilder
				= new StandardServiceRegistryBuilder(bootstrapRegistryBuilder.build())
						.configure("de/astoffel/laz/hibernate.cfg.xml");
		if (prefix.isPresent()) {
			registryBuilder.applySetting("hibernate.connection.url", String.format("jdbc:h2:%s/database", prefix.get()));
		}
		StandardServiceRegistry standardRegistry = registryBuilder.build();

		Metadata metadata = new MetadataSources(standardRegistry)
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();

		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
				.build();

		return sessionFactory;
	}

	@Override
	public void close() {
		this.sessionFactory.close();
	}

	public DataSession getSession() {
		return new DataSession(sessionFactory.getCurrentSession());
	}

	public void atomic(AtomicTx atomic) {
		atomicThrows(atomic);
	}

	public <E extends Exception> void atomicThrows(AtomicThrowsTx<E> atomic) throws E {
		atomicComputeThrows(session -> {
			atomic.atomic(session);
			return null;
		});
	}

	public <R> R atomicCompute(AtomicComputeTx<R> atomicCompute) {
		return atomicComputeThrows(atomicCompute);
	}

	public <R, E extends Exception> R atomicComputeThrows(AtomicComputeThrowsTx<R, E> atomic) throws E {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			try {
				return atomic.atomicCompute(new DataSession(session));
			} finally {
				transaction.commit();
			}
		} catch (Throwable th) {
			transaction.rollback();
			throw th;
		}
	}
}
