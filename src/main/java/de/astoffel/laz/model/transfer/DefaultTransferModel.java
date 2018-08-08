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
package de.astoffel.laz.model.transfer;

import java.nio.file.Path;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author astoffel
 */
public final class DefaultTransferModel extends AbstractTransferModel {

	private final Optional<Path> prefix;

	public DefaultTransferModel() {
		this(Optional.empty());
	}

	public DefaultTransferModel(Path prefix) {
		this(Optional.of(prefix));
	}

	private DefaultTransferModel(Optional<Path> prefix) {
		this.prefix = prefix;
		open();
	}

	@Override
	protected SessionFactory createSessionFactory() {
		var bootstrapRegistryBuilder
				= new BootstrapServiceRegistryBuilder()
						.applyIntegrator(new FlywayIntegrator());
		var registryBuilder
				= new StandardServiceRegistryBuilder(bootstrapRegistryBuilder
						.build())
						.configure("de/astoffel/laz/hibernate.cfg.xml");
		if (prefix.isPresent()) {
			registryBuilder.applySetting("hibernate.connection.url", String
					.format("jdbc:h2:%s/database", prefix.get()));
		}
		var standardRegistry = registryBuilder.build();

		var metadata = new MetadataSources(standardRegistry)
				.getMetadataBuilder()
				.applyImplicitNamingStrategy(
						ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
				.build();

		var sessionFactory = metadata.getSessionFactoryBuilder()
				.build();

		return sessionFactory;
	}

}
