import os
import shutil
import hashlib
import json

APK_SRC = r"C:\KnowTheMice\android\app\build\outputs\apk\release\app-release.apk"
PUB_BIN = r"C:\KnowTheMice\website\public\downloads\android\KnowTheMice-Android.bin"
DIST_BIN = r"C:\KnowTheMice\website\dist\downloads\android\KnowTheMice-Android.bin"
PUB_JSON = r"C:\KnowTheMice\website\public\downloads.json"
DIST_JSON = r"C:\KnowTheMice\website\dist\downloads.json"
DOWNLOAD_PAGE = r"C:\KnowTheMice\website\src\pages\Download.tsx"

def post_process():
    if not os.path.exists(APK_SRC):
        print(f"Error: {APK_SRC} does not exist!")
        return None, None

    size_bytes = os.path.getsize(APK_SRC)
    size_mb = f"{size_bytes / (1024 * 1024):.2f} MB"
    
    with open(APK_SRC, "rb") as f:
        sha256 = hashlib.sha256(f.read()).hexdigest().upper()
    
    print(f"Android APK built: {size_bytes} bytes ({size_mb})")
    print(f"Android SHA-256: {sha256}")

    # Copy to website downloads
    os.makedirs(os.path.dirname(PUB_BIN), exist_ok=True)
    os.makedirs(os.path.dirname(DIST_BIN), exist_ok=True)
    shutil.copy2(APK_SRC, PUB_BIN)
    shutil.copy2(APK_SRC, DIST_BIN)
    print("Copied APK to website downloads as .bin")

    # Update downloads.json
    for path in [PUB_JSON, DIST_JSON]:
        if os.path.exists(path):
            with open(path, "r") as f:
                data = json.load(f)
            data["android"]["sha256"] = sha256
            data["windows"]["sha256"] = "7589C7A1B1814BA508B9005D24621F6AD6A5DCDEEBE7A5EB18E6F24BFCE36B86"
            with open(path, "w") as f:
                json.dump(data, f, indent=2)
            print(f"Updated {path} with hashes")

    # Update Download.tsx
    if os.path.exists(DOWNLOAD_PAGE):
        with open(DOWNLOAD_PAGE, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Replace Android section in DEFAULT_CONFIG
        import re
        content = re.sub(
            r'android:\s*\{\s*version:\s*"[^"]*",\s*name:\s*"[^"]*",\s*filename:\s*"[^"]*",\s*url:\s*"[^"]*",\s*size:\s*"[^"]*",\s*sha256:\s*"[^"]*"',
            f'android: {{\n    version: "1.1.0",\n    name: "Know The Mice for Android",\n    filename: "KnowTheMice-Android.apk",\n    url: "/downloads/android/KnowTheMice-Android.apk",\n    size: "{size_mb}",\n    sha256: "{sha256}"',
            content
        )
        with open(DOWNLOAD_PAGE, "w", encoding="utf-8") as f:
            f.write(content)
        print("Updated Download.tsx with Android v1.1.0 and SHA-256")

    return sha256, size_mb

if __name__ == "__main__":
    post_process()
