package jpa.study.book.repository;

public interface NestedClosedProjection {
	String getUsername();

	TeamInfo getTeam();

	interface TeamInfo {
		String getName();
	}
}
