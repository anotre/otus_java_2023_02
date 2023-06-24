package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address {
  @Id
  @SequenceGenerator(name = "address_gen", sequenceName = "address_seq",
          initialValue = 1, allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
  private Long id;

  @Column
  private String street;
}
