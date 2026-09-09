$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.11"
$env:PATH = "C:\Program Files\Maven\apache-maven-3.9.14-bin\apache-maven-3.9.14\bin;C:\Program Files\Java\jdk-21.0.11\bin;$env:PATH"

$mavenArgs = $args -join " "
cmd.exe /c "mvn $mavenArgs"
