package com.cleartax.training_superheroes.dto;

import lombok.Getter;

@Getter

public class SuperheroRequestBody {
    private final String name;
    private final String power;
    private final String universe;

    SuperheroRequestBody(String name, String power, String universe) {
        this.name = name;
        this.power = power;
        this.universe = universe;
    }

    public static SuperheroRequestBodyBuilder builder() {
        return new SuperheroRequestBodyBuilder();
    }

    public static class SuperheroRequestBodyBuilder {
        private String name;
        private String power;
        private String universe;

        SuperheroRequestBodyBuilder() {
        }

        public SuperheroRequestBodyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SuperheroRequestBodyBuilder power(String power) {
            this.power = power;
            return this;
        }

        public SuperheroRequestBodyBuilder universe(String universe) {
            this.universe = universe;
            return this;
        }

        public SuperheroRequestBody build() {

            return new SuperheroRequestBody(this.name, this.power, this.universe);
        }

        public String toString() {
            return "SuperheroRequestBody.SuperheroRequestBodyBuilder(name=" + this.name + ", power=" + this.power + ", universe=" + this.universe + ")";
        }
    }
}