set "javaOpts=-Xms512m -Xmx1g"
set "javaOpts=%javaOpts% -Dmirai.no-desktop"
set "javaOpts=%javaOpts% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%cd%/oom"
java %javaOpts% -jar %cd%\target\cloud-serverless-1.0.jar
