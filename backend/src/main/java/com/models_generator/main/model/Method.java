package com.models_generator.main.model;

import com.models_generator.main.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Method {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String returnType;

    @Enumerated(EnumType.STRING)
    private Visibility  visibility;

    @ElementCollection
    private List<String> parameters;

    @ManyToOne
    private ClassEntity classEntity;
}
