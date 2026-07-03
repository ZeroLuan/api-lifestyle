package br.com.sysmap.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

// Ponto 4 corrigido: @Setter removido da classe. Apenas campos mutáveis de negócio
// recebem @Setter individualmente. Campos de auditoria (id, createdAt) nunca devem ser alterados externamente.
@Entity
@Table(name = "activities")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Activities extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private ActivityAddresses address;

    @Setter
    @Column(name = "image", nullable = false)
    private String image;

    @Setter
    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Setter
    @Column(name = "confirmation_code", length = 6)
    private String confirmationCode;

    @Setter
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Setter
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users creator;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ActivityTypes type;

    @OneToMany(mappedBy = "activity")
    private List<ActivityParticipants> participants;
}
