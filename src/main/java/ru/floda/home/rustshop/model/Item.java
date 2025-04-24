package ru.floda.home.rustshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private Long gameId;

    private String icon;

    private String nameRu;


}
