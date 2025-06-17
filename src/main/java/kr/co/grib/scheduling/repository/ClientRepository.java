package kr.co.grib.scheduling.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.grib.scheduling.domain.Client;

public interface ClientRepository extends JpaRepository<Client, String> {
    Page<Client> findAllByClientIdContains(Pageable pageable, String clientId);
    Page<Client> findAllByCallbackUrlContains(Pageable pageable, String clientIdIssuedAt);
}
