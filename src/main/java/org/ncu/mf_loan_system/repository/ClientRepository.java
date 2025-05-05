package org.ncu.mf_loan_system.repository;

import org.ncu.mf_loan_system.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {

}