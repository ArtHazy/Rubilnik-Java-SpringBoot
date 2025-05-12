#!/bin/bash

# Get Wi-Fi interface (change if needed)
wifi_interface="en0"

# Get current SSID
ssid=$(networksetup -getairportnetwork "$wifi_interface" | sed 's/^Current Wi-Fi Network: //')

# Get password from Keychain
password=$(security find-generic-password -D "AirPort network password" -a "$ssid" -gw 2>/dev/null)

# Output
echo "Current Wi-Fi SSID: $ssid"
if [ -n "$password" ]; then
    echo "Password for $ssid: $password"
else
    echo "Password for $ssid not found in keychain or access denied."
fi