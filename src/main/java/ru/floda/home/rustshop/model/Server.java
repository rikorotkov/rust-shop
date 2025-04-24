package ru.floda.home.rustshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serverName;

    private String serverIp;

    private String serverPort;

    private String rconIp;

    private String rconPort;

    private String rconPassword;

    @OneToMany
    private List<Kit> serverKits = new ArrayList<>();

    @OneToMany
    private List<Item> serverItems = new ArrayList<>();
}
