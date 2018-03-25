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

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 *
 * @author astoffel
 */
final class FlywayIntegrator implements Integrator {

	@Override
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL((String) sessionFactory.getProperties().get("hibernate.connection.url"));
		dataSource.setUser((String) sessionFactory.getProperties().get("connection.username"));
		dataSource.setPassword((String) sessionFactory.getProperties().get("connection.password"));
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.setLocations("classpath:de/astoffel/laz/db");
		flyway.migrate();
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
	}

}
