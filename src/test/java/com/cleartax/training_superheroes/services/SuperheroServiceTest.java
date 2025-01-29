package com.cleartax.training_superheroes.services;

import com.cleartax.training_superheroes.dto.Superhero;
import com.cleartax.training_superheroes.repos.SuperheroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SuperheroServiceTest {

    @Mock
    private SuperheroRepository superheroRepository;

    @InjectMocks
    private SuperheroService superheroService; // its dependency are still of MOck

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks and inject them
    }


    @Test
    void testGetSuperheroByNameAndUniverse() {
        // Arrange
        String name = "Batman";
        String universe = "DC";

        // Create a mock superhero to simulate a database entry
        Superhero mockHero = new Superhero();
        mockHero.setName(name);
        mockHero.setUniverse(universe);
        mockHero.setPower("Detective");

        // Define the behavior of the mocked repository
        when(superheroRepository.findByNameAndUniverse(name, universe)).thenReturn(mockHero);

        // Act
        Superhero result = superheroService.getSuperhero(name, universe);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(universe, result.getUniverse());
        assertEquals("Detective", result.getPower());

    }




}