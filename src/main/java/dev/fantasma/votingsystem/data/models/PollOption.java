package dev.fantasma.votingsystem.data.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "poll_option",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"poll_id", "label"}
    )
)
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollOption option)) return false;
        return id != null && Objects.equals(id, option.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
