package br.com.sysmap.backend.entity;

import br.com.sysmap.backend.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_participants")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipants extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;

    @Setter
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Setter
    @Column(name = "approved")
    private Boolean approved;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activities activity;
}
