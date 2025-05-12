# Get current SSID
$ssid = (netsh wlan show interfaces | Select-String -Pattern "SSID" | ForEach-Object { $_.Line.Split(":")[1].Trim() })

# Get saved Wi-Fi profiles
$profiles = netsh wlan show profiles

# Find the profile that matches the SSID
$profile = ($profiles | Select-String -Pattern $ssid).Line.Split(":")[1].Trim()

# Get the Wi-Fi password from the saved profile
$password = (netsh wlan show profile name="$profile" key=clear | Select-String -Pattern "Key Content" | ForEach-Object { $_.Line.Split(":")[1].Trim() })

# Output results
if ($ssid) {
    Write-Host "Current Wi-Fi SSID: $ssid"
} else {
    Write-Host "No active Wi-Fi connection found."
}

if ($password) {
    Write-Host "Password for $ssid: $password"
} else {
    Write-Host "Password for $ssid not found or access denied."
}
