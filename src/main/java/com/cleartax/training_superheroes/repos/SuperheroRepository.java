package com.cleartax.training_superheroes.repos;

import com.cleartax.training_superheroes.dto.Superhero;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SuperheroRepository extends MongoRepository<Superhero, String> {
    Superhero findByName(String name);

    Superhero findByUniverse(String universe);

    Superhero findByNameAndUniverse(String name, String universe);
//    void deleteByName(String name);
//
//    Optional<Superhero> findByName(String name);
}
