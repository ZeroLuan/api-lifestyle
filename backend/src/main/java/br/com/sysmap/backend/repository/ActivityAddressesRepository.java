package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.ActivityAddresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityAddressesRepository extends JpaRepository<ActivityAddresses, UUID> {
}
