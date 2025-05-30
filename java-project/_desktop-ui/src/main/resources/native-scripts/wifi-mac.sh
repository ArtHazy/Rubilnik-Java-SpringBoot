#!/bin/bash

# Get the log file path from the first argument
log_file="$1"
> "$log_file"  # Clear the log file

# Wi-Fi interface (change if needed)
wifi_interface="en0"

# Get current SSID
ssid=$(networksetup -getairportnetwork "$wifi_interface" | sed 's/^Current Wi-Fi Network: //')
if [ -n "$ssid" ]; then
  echo "$ssid" >> "$log_file"
else
  echo "No active Wi-Fi connection found." >> "$log_file"
fi

# Get password from Keychain
password=$(security find-generic-password -D "AirPort network password" -a "$ssid" -gw)
if [ -n "$password" ]; then
  echo "$password" >> "$log_file"
else
  echo "" >> "$log_file"
fi

# Get encryption type
// TODO
airport_path="/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport"
encryption=$( "$airport_path" -I | awk -F': ' '/link auth/ {print $2}' )

if [ -n "$encryption" ]; then
  echo "$encryption" >> "$log_file"
else
  echo "" >> "$log_file"
fi