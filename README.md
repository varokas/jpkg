# Jpkg

## Requires
* Java 11

## Run
```
$ ./gradlew clean build
$ ./gradlew jlink

$ cat ./build/jlink/bin/jpkg

#!/bin/sh
JLINK_VM_OPTIONS=
DIR=`dirname $0`
$DIR/java $JLINK_VM_OPTIONS -m jpkg/jpkg.AppKt $@

$ ./build/jlink/bin/jpkg

Hello: 0.173754630778555

```

## Reference
* https://github.com/ilya-g/kotlin-jlink-examples
* https://docs.gradle.org/current/userguide/application_plugin.html
* http://tutorials.jenkov.com/java/modules.html