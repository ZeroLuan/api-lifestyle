package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.ActivityTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityTypesRepository extends JpaRepository<ActivityTypes, UUID> {
}
