package br.com.sysmap.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "activity_addresses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAddresses extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "address", nullable = false)
    private String address;

    @Setter
    @Column(name = "latitude")
    private Double latitude;

    @Setter
    @Column(name = "longitude")
    private Double longitude;

    @OneToOne(mappedBy = "address")
    private Activities activity;
}

