package ru.floda.home.rustshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.floda.home.rustshop.repository.ServerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RconService {

    private final ServerRepository serverRepository;

}
