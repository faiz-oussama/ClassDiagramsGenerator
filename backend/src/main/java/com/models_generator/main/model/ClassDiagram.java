package com.models_generator.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ClassDiagram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "classDiagram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassEntity> classes;

    @OneToMany(mappedBy = "classDiagram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> relationships;
}
