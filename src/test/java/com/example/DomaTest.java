package com.example;

import org.junit.Test;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.tx.TransactionManager;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * DOMA 単体では問題が起きないことを確認するためのテスト
 */
public class DomaTest {

    private static int L_MAX = 100;


    @Test
    public void getCountryOne() throws Exception {
        getCountrySub("getCountryOne");
    }

    @Test
    public void getCountrySingleThread() throws Exception {
        // DomaSpringBootWebTest クラスの同名メソッドと同内容にするためのsleep
        Thread.sleep(1000);
        for (int i = 0; i < L_MAX; i++) {
            getCountrySub("getCountrySingleThread(" + i + ")");
        }
    }

    @Test
    public void getCountryMultiThread() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> getCountrySub("getCountryMultiThread(" + i + ")"));
    }

    private void getCountrySub(String message) {
        CountryDomaDao countryDao = new CountryDomaDaoImpl(AppConfig.singleton());
        TransactionManager tm = AppConfig.singleton().getTransactionManager();
        try {
            tm.required(() -> {
                SelectOptions options = SelectOptions.get().count();
                List<CountryEntity> result = countryDao.select(options);
                assertThat("result.size()", result.size(), is(4));
                assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
            });
        } catch (JdbcException e) {
            throw new RuntimeException("MySQL接続エラー。com.example.AppConfig のjavaソースに記載されているdb接続情報を確認してください", e);
        }
    }


}
