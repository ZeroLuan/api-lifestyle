package br.com.sysmap.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Setter
    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "xp", nullable = false)
    private Integer xp = 0;

    @Column(name = "level", nullable = false)
    private Integer level = 0;

    @OneToMany(mappedBy = "user")
    private List<Preferences> preferences;

    @OneToMany(mappedBy = "user")
    private List<UserAchievements> achievements;

    @OneToMany(mappedBy = "creator")
    private List<Activities> activities;

    @OneToMany(mappedBy = "user")
    private List<ActivityParticipants> participations;

    /**
     * Adiciona XP e verifica automaticamente se o usuario subiu de nivel.
     */
    public void addXp(Integer amount) {
        if (amount == null || amount < 0) {
            return;
        }

        this.xp += amount;

        while (this.xp >= getXpForNextLevel()) {
            promoteLevel();
        }
    }

    /**
     * Calcula o XP necessario para o proximo nivel.
     * Exemplo: Nivel 1 precisa de 100, Nivel 2 de 200...
     */
    private Integer getXpForNextLevel() {
        return (this.level + 1) * 100;
    }

    /**
     * Incrementa o nivel do usuario.
     */
    private void promoteLevel() {
        this.level++;
    }
}
