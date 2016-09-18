package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DomaCountTestApplication.class)
public class DomaSpringTest {

    @Autowired
    CountrySpringDao countryDao;

    private static int L_MAX = 100;

    /**
     * doma-spring-boot-starter で１回のみの実行の場合正しく動作することを確認するテスト
     */
    @Test
    public void getCountryOne() throws Exception {
        getCountrySub("getCountryOne");
    }

    /**
     * doma-spring-boot-starter で1スレッドで実行した場合は、連続しても問題ないことを確認するテスト
     */
    @Test
    public void getCountrySingleThread() throws Exception {
        // クラス単位でtest実行すると getCountryMultiThread の thread と被って fail する場合があるので少しsleep
        Thread.sleep(1000);

        for (int i = 0; i < L_MAX; i++) {
            getCountrySub("getCountrySingleThread(" + i + ")");
        }
    }

    /**
     * doma-spring-boot-starter で複数スレッドで実行した場合、SelectOptions#getCount が正常動作
     * しないことを確認するためのテスト
     */
    @Test
    public void getCountryMultiThread() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> getCountrySub("getCountryMultiThread(" + i + ")"));
    }

    private void getCountrySub(String message) {
        SelectOptions options = SelectOptions.get().count();
        List<CountryEntity> result = countryDao.select(options);
        assertThat("result.size()", result.size(), is(4));
        assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
    }

}