$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.11"
$env:PATH = "C:\Program Files\Maven\apache-maven-3.9.14-bin\apache-maven-3.9.14\bin;C:\Program Files\Java\jdk-21.0.11\bin;$env:PATH"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  ElderCare Connect - Launcher" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# Check if port 8080 is in use and stop existing process
$connection = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($connection) {
    $pids = $connection | Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($p in $pids) {
        if ($p -gt 0) {
            Write-Host "Stopping existing process on port 8080 (PID: $p)..." -ForegroundColor Yellow
            Stop-Process -Id $p -Force -ErrorAction SilentlyContinue
        }
    }
    Start-Sleep -Seconds 1
}

Write-Host "Starting ElderCare Connect on http://localhost:8080 ..." -ForegroundColor Green
& "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" -jar "target\eldercare-connect-0.0.1-SNAPSHOT.jar"
