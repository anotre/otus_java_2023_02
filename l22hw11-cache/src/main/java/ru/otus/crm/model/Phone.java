package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Phone {
  @Id
  @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq",
          initialValue = 1, allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
  private Long id;

  @Column
  private String number;
}
