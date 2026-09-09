@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "PATH=C:\Program Files\Maven\apache-maven-3.9.14-bin\apache-maven-3.9.14\bin;C:\Program Files\Java\jdk-21.0.11\bin;%PATH%"

call "C:\Program Files\Maven\apache-maven-3.9.14-bin\apache-maven-3.9.14\bin\mvn.cmd" %*
