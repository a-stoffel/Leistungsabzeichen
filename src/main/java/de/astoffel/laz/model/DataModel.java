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

/**
 *
 * @author astoffel
 */
public interface DataModel extends AutoCloseable {

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
	
	@Override
	public void close();

	public default void atomic(AtomicTx atomic) {
		atomicThrows(atomic);
	}

	public default <R> R atomicCompute(AtomicComputeTx<R> atomicCompute) {
		return atomicComputeThrows(atomicCompute);
	}

	public <R, E extends Exception> R atomicComputeThrows(AtomicComputeThrowsTx<R, E> atomic) throws E;

	public <E extends Exception> void atomicThrows(AtomicThrowsTx<E> atomic) throws E;

}
