package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.repository.DonationRepository;
import ru.floda.home.rustshop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final UserRepository userRepository;
    private final DonationRepository donationRepository;

}
