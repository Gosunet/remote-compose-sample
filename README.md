# Remote Compose Sample

A small Android app and Ktor server demonstrating server-generated Remote Compose documents.

## Projects

- `:android-app` is a Compose screen with a bottom action button and a Remote Compose player.
- `:server` is a Ktor CIO server that serves a raw Remote Compose document at `GET /remote-compose`.
- `:remote-documents` is shared JVM code for the clock, weather, note, and message documents. Keeping the writer here lets the server and Android Studio previews render the exact same documents.

Each request to `/remote-compose` cycles through the four documents. The weather values and time are static sample data.

## Run

Use JDK 25 with Android SDK platform 37 installed. The shared document and server modules use a JDK 25 toolchain. The shared document module emits Java 17 bytecode so it remains consumable by Android's D8 compiler; the server uses the JDK 25 default bytecode target.

```shell
./gradlew :server:run
```

Leave the server running while using the app. For an Android emulator, create a reverse port forward so the app's `127.0.0.1:8080` request reaches the development machine:

```shell
adb reverse tcp:8080 tcp:8080
```

The app connects to `http://127.0.0.1:8080/remote-compose` by default. For a physical device on the same network, change `REMOTE_COMPOSE_URL` in `android-app/build.gradle.kts` to the development machine's LAN address and ensure port 8080 is reachable.

The Android Studio preview panel includes a preview for each document. Preview generation runs the shared JVM writer in the Android module and then renders its bytes with the Remote Compose player.
