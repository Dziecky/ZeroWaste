package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diy")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Diy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String beforeImageUrl;
    private String afterImageUrl;
    private String description;
    @Column(columnDefinition = "TEXT") // Użyj TEXT dla dłuższych tekstów w bazie
    private String fullDescription;
}
