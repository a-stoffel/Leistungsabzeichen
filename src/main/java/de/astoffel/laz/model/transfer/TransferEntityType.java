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
package de.astoffel.laz.model.transfer;

/**
 *
 * @author astoffel
 */
public final class TransferEntityType<E extends TransferEntity> {

	public static final TransferEntityType<TransferAssessment> ASSESSMENT
			= new TransferEntityType<>("Assessment", TransferSession::assessments);
	public static final TransferEntityType<TransferCategory> CATEGORY
			= new TransferEntityType<>("Category", TransferSession::categories);
	public static final TransferEntityType<TransferExam> EXAM
			= new TransferEntityType<>("Exam", TransferSession::exams);
	public static final TransferEntityType<TransferGrade> GRADE
			= new TransferEntityType<>("Grade", TransferSession::grades);
	public static final TransferEntityType<TransferInstrument> INSTRUMENT
			= new TransferEntityType<>("Instrument", TransferSession::instruments);
	public static final TransferEntityType<TransferJury> JURY
			= new TransferEntityType<>("Jury", TransferSession::juries);
	public static final TransferEntityType<TransferMeta> META
			= new TransferEntityType<>("Meta", TransferSession::meta);
	public static final TransferEntityType<TransferParticipant> PARTICIPANT
			= new TransferEntityType<>("Participant", TransferSession::participants);
	public static final TransferEntityType<TransferParticipation> PARTICIPATION
			= new TransferEntityType<>("Participation", TransferSession::participations);

	@FunctionalInterface
	public static interface EntitySetAccessor<E extends TransferEntity> {

		EntitySet<E> entitySet(TransferSession session);
	}

	private final String name;
	private final EntitySetAccessor<E> accessor;

	public TransferEntityType(String name, EntitySetAccessor<E> accessor) {
		this.name = name;
		this.accessor = accessor;
	}

	public String name() {
		return name;
	}

	public EntitySet<E> entitySet(TransferSession session) {
		return accessor.entitySet(session);
	}

}
