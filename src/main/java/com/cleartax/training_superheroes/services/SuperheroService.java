package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.dto.Superhero;
import com.cleartax.training_superheroes.dto.SuperheroRequestBody;
import com.cleartax.training_superheroes.repos.SuperheroRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class SuperheroService {

    private SuperheroRepository superheroRepository;


    public Superhero getSuperhero(String name, String universe) {
        System.out.println("testing is started and query parameters" + universe + " " + name);
        if (name != null && universe != null) {
            return getByNameAndUniverse(name, universe);
        } else if (name != null) {
            return getByName(name);
        } else if (universe != null) {
            return getByUniverse(universe);
        } else {
            return null;
        }
    }

    public Superhero updateSuperhero(String name , String universe , SuperheroRequestBody updateDeatails){
            Superhero superhero = getByName(name);
            superhero.setName(updateDeatails.getName());
            superhero.setUniverse(updateDeatails.getUniverse());
            return superheroRepository.save(superhero);
    }

    public Superhero getByName(String name){
        Superhero superhero = superheroRepository.findByName(name);
        if(null != superhero){
            System.out.println("You are checking superHero name " + superhero);
            return superhero;
        }
        else{
            System.out.println("Superhero not found when trying to get superHero name ");
            return null;
        }
    }

    private Superhero  getByUniverse(String universe){
        Superhero superhero = superheroRepository.findByUniverse(universe);
        if(null != superhero){
            return superhero;
        }
        else{
            System.out.println("Superhero not found when trying to get superHero universe ");
            return null;
        }
    }

    public   Superhero getByNameAndUniverse(String name, String universe){
        Superhero superhero = superheroRepository.findByNameAndUniverse(name, universe);
        if (superhero == null) {
            System.out.println("Superhero not found when trying to access getByNameAndUniverse ");
            return null;

        }
        return superhero;
    }

public boolean deleteSuperhero(String name, String universe) {
    Superhero superhero = superheroRepository.findByNameAndUniverse(name, universe);
    if (superhero != null) {
        superheroRepository.delete(superhero);
        return true;
    } else {
        throw new RuntimeException("Superhero not found with name: " + name + " and universe: " + universe);
    }
}

    public Superhero persistSuperhero(SuperheroRequestBody requestBody){
        Superhero superhero = new Superhero();
        superhero.setName(requestBody.getName());
        superhero.setPower(requestBody.getPower());
        superhero.setUniverse(requestBody.getUniverse());
        return superheroRepository.save(superhero);
    }


}

