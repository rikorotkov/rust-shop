package ru.floda.home.rustshop.service;

import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.repository.DonationRepository;
import ru.floda.home.rustshop.repository.UserRepository;

@Service
public class DonationService {

    private final UserRepository userRepository;
    private final DonationRepository donationRepository;


    public DonationService(UserRepository userRepository, DonationRepository donationRepository) {
        this.userRepository = userRepository;
        this.donationRepository = donationRepository;
    }
}
