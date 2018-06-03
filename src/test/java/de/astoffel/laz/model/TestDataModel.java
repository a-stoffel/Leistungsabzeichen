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
public final class TestDataModel implements DataModel {

	private final SessionFactory sessionFactory;

	public TestDataModel() {
		this.sessionFactory = open();
	}

	private static SessionFactory open() {
		BootstrapServiceRegistryBuilder bootstrapRegistryBuilder
				= new BootstrapServiceRegistryBuilder()
						.applyIntegrator(new FlywayIntegrator());
		StandardServiceRegistryBuilder registryBuilder
				= new StandardServiceRegistryBuilder(bootstrapRegistryBuilder.build())
						.configure("de/astoffel/laz/hibernate.test.cfg.xml");
		StandardServiceRegistry standardRegistry = registryBuilder
				.build();

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

	@Override
	public <E extends Exception> void atomicThrows(AtomicThrowsTx<E> atomic) throws E {
		atomicComputeThrows(session -> {
			atomic.atomic(session);
			return null;
		});
	}

	@Override
	public <R, E extends Exception> R atomicComputeThrows(AtomicComputeThrowsTx<R, E> atomic) throws E {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			R result = atomic.atomicCompute(new DataSession(session));
			transaction.commit();
			return result;
		} catch (Throwable th) {
			transaction.rollback();
			throw th;
		}
	}
}