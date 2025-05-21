# Define log file path
param (
    [string]$logFile = "C:\Users\artem\OneDrive\Desktop\output.txt"
)

Clear-Content -Path $logFile

# Get current SSID
$ssid = (netsh wlan show interfaces | Select-String -Pattern "^\s+SSID\s*:" | ForEach-Object { $_.Line.Split(":")[1].Trim() })

# Get saved Wi-Fi profiles
$profiles = netsh wlan show profiles

# Find the profile that matches the SSID
$profile = ($profiles | Select-String -Pattern $ssid).Line.Split(":")[1].Trim()

# Get the Wi-Fi password from the saved profile
$password = (netsh wlan show profile name="$profile" key=clear | Select-String -Pattern "Key Content" | ForEach-Object { $_.Line.Split(":")[1].Trim() })

# Get encryption type (Authentication)
$auth = (netsh wlan show profile name="$profile" key=clear | Select-String -Pattern "Authentication" | ForEach-Object { $_.Line.Split(":")[1].Trim() })

# Output results to log
if ($ssid) {
    Add-Content -Path $logFile -Value "${ssid}"
} else {
    Add-Content -Path $logFile -Value "No active Wi-Fi connection found."
}

if ($password) {
    Add-Content -Path $logFile -Value "${password}"
} else {
    Add-Content -Path $logFile -Value ""
}

if ($auth) {
    Add-Content -Path $logFile -Value "${auth}"
} else {
    Add-Content -Path $logFile -Value "Encryption type not found."
}
