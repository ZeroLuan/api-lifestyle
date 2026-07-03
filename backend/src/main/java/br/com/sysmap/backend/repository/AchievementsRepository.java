package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.Achievements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementsRepository extends JpaRepository<Achievements, UUID> {
    Optional<Achievements> findByName(String name);
}
