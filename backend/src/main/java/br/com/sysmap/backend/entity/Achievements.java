package br.com.sysmap.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "achievements")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Achievements extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "criterion", nullable = false)
    private String criterion;
}
