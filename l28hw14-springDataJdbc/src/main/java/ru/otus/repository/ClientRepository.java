package ru.otus.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import ru.otus.model.Client;

import java.util.List;

@Repository
public interface ClientRepository extends ListCrudRepository<Client, Long> {
    @Override
    @Query(value = """
       select c.id         as client_id,
              c.name       as client_name,
              a.id         as address_id,
              a.street     as street,
              p.id         as phone_id,
              p.number     as number
       from clients c
       left outer join addresses a
              on c.id = a.client_id
       left outer join phones p
              on c.id = p.client_id
       order by c.id
       """, 
       resultSetExtractorClass = ClientResultSetExtractor.class)
    List<Client> findAll();
}
