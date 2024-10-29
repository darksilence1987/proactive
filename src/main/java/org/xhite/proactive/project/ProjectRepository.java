package org.xhite.proactive.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.xhite.proactive.user.AppUser;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN p.projectMembers m WHERE m.id = :memberId AND p.status = :status")
    List<Project> findProjectsByMemberIdAndStatus(Long memberId, ProjectStatus status);

    List<Project> findProjectsByCreatedByAndStatus(AppUser createdBy, ProjectStatus status);

    Project findProjectByIdAndStatus(Long id, ProjectStatus status);

    List<Project> findAllByStatusIs(ProjectStatus status);
}
