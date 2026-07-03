package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, UUID> {

    List<Preferences> findAllByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM Preferences p WHERE p.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
