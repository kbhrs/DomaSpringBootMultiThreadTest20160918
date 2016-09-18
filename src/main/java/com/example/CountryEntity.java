package com.example;

import org.seasar.doma.Entity;
import org.seasar.doma.Table;
import org.seasar.doma.jdbc.entity.NamingType;

@Entity(naming = NamingType.SNAKE_LOWER_CASE)
@Table(name = "country")
public class CountryEntity {
    private String countryName;

    public String getCountryName() {
        return countryName;
    }
}
