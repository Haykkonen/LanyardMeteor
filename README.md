# Lanyard :: Meteor Client Addon

[![License: CC0-1.0](https://img.shields.io/badge/License-CC0%201.0-lightgrey.svg)](http://creativecommons.org/publicdomain/zero/1.0/)

Lanyard is designed to be a robust, high-quality tool for developers and security researchers using Meteor. It provides a suite of modules focused on network analysis, development testing, and utilities.

## Core Features

The features are divided into two main categories: analysis utilities and stress-testing modules.

### 🔬 Network Analysis & Utilities

*   **PacketLogger**: A powerful packet analyzer that allows you to inspect incoming (`S2C`) and outgoing (`C2S`) packets in real-time. Essential for debugging client-server communication, understanding plugin behavior, or identifying network vulnerabilities.
*   **PacketDelay**: Queues and delays the dispatch of specific `C2S` packets. Ideal for simulating latency (lag), testing how a server handles out-of-order packets, or analyzing the impact of timing on certain interactions.
*   **GuiHelper**: A quality-of-life utility that adds a chat interface to any open inventory or GUI screen. It allows you to execute commands and communicate without interrupting interactions with chests, crafting tables, etc.

### 💥 Crasher / Stress-Testing Modules

These modules are designed for penetration testing and assessing server resilience against abnormal packet loads.

*   **CommandFlood Crasher**: Floods the server with command execution packets (`CommandExecutionC2SPacket`), utilizing a fully configurable payload and spam buffer. Useful for testing a server's command processing and anti-spam protection.
*   **Velocity Crasher**: A specialized stress-testing module that aims to overload Velocity (and similar) proxies by spamming the `/server` command. Designed to evaluate a proxy's ability to handle a high volume of server-switching requests.

## Installation

1.  **Prerequisite**: You must have [Meteor Client](https://meteorclient.com/) installed.
2.  Download the latest release of Lanyard (`.jar`) from the [Releases page](https://github.com/YOUR_USERNAME/YOUR_REPOSITORY/releases).
3.  Place the `.jar` file into the `addons` folder inside your Meteor Client directory (`.minecraft/meteor-client/addons`).
4.  Launch Minecraft. The addon will be loaded automatically.

## ⚠️ Disclaimer & Ethical Use

This addon is a tool intended for **educational, research, and development purposes only**. The stress-testing modules (`Crashers`) should be used **EXCLUSIVELY** on servers where you have explicit permission to conduct penetration tests.

The misuse of these tools on third-party servers without authorization is illegal and unethical. The author assumes no responsibility for any damage or consequences resulting from the misuse of this software. **Use responsibly.**

## For Developers (Building from Source)

If you wish to modify or compile Lanyard from source, follow these steps:

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git

# Navigate into the project directory
cd Lanyard

# Build the project using the Gradle wrapper
./gradlew build
