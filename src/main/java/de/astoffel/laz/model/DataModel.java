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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author andreas
 */
public final class DataModel implements AutoCloseable {

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
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
				.configure("de/astoffel/laz/hibernate.cfg.xml");
		if (prefix.isPresent()) {
			registryBuilder.applySetting("hibernate.connection.url", String.format("jdbc:h2:%s/database", prefix.get()));
			if (Files.exists(prefix.get().resolve("database.mv.db"))) {
				registryBuilder.applySetting("hibernate.hbm2ddl.auto", "update");
			}
		}
		StandardServiceRegistry standardRegistry = registryBuilder.build();

		Metadata metadata = new MetadataSources(standardRegistry)
				.addAnnotatedClass(Assessment.class)
				.addAnnotatedClass(Category.class)
				.addAnnotatedClass(Exam.class)
				.addAnnotatedClass(Grade.class)
				.addAnnotatedClass(Instrument.class)
				.addAnnotatedClass(Jury.class)
				.addAnnotatedClass(Meta.class)
				.addAnnotatedClass(Participant.class)
				.addAnnotatedClass(Participation.class)
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

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
