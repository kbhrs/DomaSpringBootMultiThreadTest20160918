package com.example;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.jdbc.SelectOptions;

import java.util.List;

/**
 * Springに依存しないDAO
 */
@Dao
public interface CountryDomaDao {
    @Select
    List<CountryEntity> select(SelectOptions options);
}
