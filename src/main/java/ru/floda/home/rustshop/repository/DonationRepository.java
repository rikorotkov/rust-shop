package ru.floda.home.rustshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.floda.home.rustshop.model.Donation;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Optional<Donation> findDonationById(Long donationId);

    List<Donation> findAll();
}
