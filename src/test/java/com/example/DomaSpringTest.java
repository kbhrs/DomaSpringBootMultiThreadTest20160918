package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
     * <p>
     * この実装方法は適切ではないのでテストが失敗する。<br/>
     * 少なくとも MySQL では、 SelectOptions＃getCount は、
     * getCount 対象のselect と同一のトランザクションで実行する必要がある（？）
     * </p>
     */
    @Test
    public void getCountryMultiThread() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> getCountrySub("getCountryMultiThread(" + i + ")"));
    }

    public void getCountrySub(String message) {
        SelectOptions options = SelectOptions.get().count();
        List<CountryEntity> result = countryDao.select(options);
        assertThat("result.size()", result.size(), is(4));
        assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
    }

    /**
     * doma-spring-boot-starter で複数スレッドで実行した場合、SelectOptions#getCount が正常動作
     * しないことを確認するためのテスト
     * <p>
     * この実装方法は適切ではないのでテストが失敗する。<br/>
     * 少なくとも MySQL では、 SelectOptions＃getCount は、
     * getCount 対象のselect と同一のトランザクションで実行する必要がある（？）<br/>
     * この実装方法は @Transactional の使い方が間違っているので
     * 「getCount 対象のselect と同一のトランザクションで実行する」になっていないので
     * failになる。（@Transactional は DI しているインスタンス経由でメソッド呼び出さないとダメ）
     * </p>
     */
    @Test
    public void getCountryMultiThread_methodTx() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> getCountrySub("getCountryMultiThread(" + i + ")"));
    }

    @Transactional
    public void getCountrySub_tx(String message) {
        SelectOptions options = SelectOptions.get().count();
        List<CountryEntity> result = countryDao.select(options);
        assertThat("result.size()", result.size(), is(4));
        assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
    }

    @Autowired
    DomaSpringTransactionalTest domaSpringTransactionalTest;

    /**
     * トランザクション管理すれば正しく動作することを確認するテスト
     * @throws Exception
     */
    @Test
    public void getCountryMultiThread_transactional() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> domaSpringTransactionalTest.getCountrySub("getCountryMultiThread_transactional(" + i + ")"));
    }

    /**
     * Read Only Transactionでも良いことを確認するためのテスト
     * @throws Exception
     */
    @Test
    public void getCountryMultiThread_transactional_read_only() throws Exception {
        IntStream.range(0, L_MAX)
                .parallel()
                .forEach(i -> domaSpringTransactionalTest.getCountrySubReadOnly("getCountryMultiThread_transactional_read_only(" + i + ")"));
    }
}

@Component
class DomaSpringTransactionalTest {
    @Autowired
    CountrySpringDao countryDao;

    @Transactional
    public void getCountrySub(String message) {
        SelectOptions options = SelectOptions.get().count();
        List<CountryEntity> result = countryDao.select(options);
        assertThat("result.size()", result.size(), is(4));
        assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
    }

    @Transactional(readOnly = true)
    public void getCountrySubReadOnly(String message) {
        SelectOptions options = SelectOptions.get().count();
        List<CountryEntity> result = countryDao.select(options);
        assertThat("result.size()", result.size(), is(4));
        assertThat("options.getCount()[" + message + "]", options.getCount(), is(4L));
    }
}