package hu.unideb.inf.printingservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.naming.Name;
import java.util.Set;

@Entity
@Table(name = "ARAJANLAT")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArajanlatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long suly;

    @Column
    private int ar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    // Fontos hogy ez nem engedi meg több anyag használatát egy nyomtatáshoz
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private MaterialEntity material;

}
