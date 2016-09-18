# DomaSpringBootMultiThreadTest20160918
doma-spring-boot-starter:1.1.0 を使用してマルチスレッドで検索を実行した場合に SelectOptions#getCount が正しい結果を返さない場合があることをテストするためのソースコード
## 含まれている内容
以下の２種類の内容が含まれる
* テスト実行に必要なテーブル作成スクリプト及びデータ作成スクリプト（flyway を使用してSpring起動時に作成）
* doma-spring-boot-starter を使ったテスト（＝現象再現用）
* Springを使わずに DOMA 単体でのテスト（＝DOMAの問題ではないことを確認するため）

以下の物は含まれていないので事前に作成する必要がある
* MySQL サーバ 及び 接続ユーザ 、database

# 事前準備
## 共通
* MySQL サーバ 及び 接続ユーザ 、database

## DOMAの実行環境設定
### IDE として Intellij を使う場合
* Menu bar > File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors
    * Enable annotation processing -> check on
    * Store generated sources relative to: Module content root
* 上記設定後 compile を実行すると src/main/generated というフォルダができる。
src/main/generated を右クリックして Mark Directory as ... から Generated Source Root に設定する。

### IDE として Intellij 以外を使う場合
* 不明

## Database接続情報の設定
### doma-spring-boot-starter を使ったテスト実行用
Spring の流儀に従い「spring.datasource.url」「spring.datasource.username」「spring.datasource.password」を設定
### DOMA 単体でのテスト実行用
src/main/java/com/example/AppConfig.java 内に datasource url username password を記載する
## テスト実行
JUnit で以下のテストクラスを実行する
* doma-spring-boot-starter を使ったテスト実行用
    * src/test/com/example/DomaSpringTest
* DOMA 単体でのテスト実行用
    * src/test/com/example/DomaTest
