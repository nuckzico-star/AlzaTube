# AlzaTube (starter project)

This is a simple Android phone app that opens YouTube's mobile website in a WebView,
with a button to open the official YouTube app. It is a starter build, not an official
YouTube client.

## Build using only your phone
1. In your phone browser, create/sign in to a GitHub account.
2. Create a **public** repository named `AlzaTube` (or a private repo if your account
   supports GitHub Actions for it).
3. Upload all files and folders from this project, preserving their paths.
4. Open the repository's **Actions** tab and run **Build AlzaTube APK** (or push a commit).
5. When the workflow completes, open its run, scroll to **Artifacts**, and download
   `AlzaTube-debug-apk`.
6. Extract the ZIP and install `app-debug.apk`. Android may ask you to allow installs
   from that browser/file manager.

## Important limitations
- This app is for playback on the phone. It does not implement Android Auto integration.
- Android Auto does not generally allow ordinary video apps to show video on its car
  display. A normal APK cannot override that restriction. Do not use video while driving.
- YouTube sign-in or playback inside WebView may be limited by Google's security and
  YouTube's web behavior. If so, use the button to open the official YouTube app.
- This starter app is unsigned for release; the debug APK is suitable for testing only.
