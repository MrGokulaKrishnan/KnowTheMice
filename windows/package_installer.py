import os
import shutil
import zipfile
import subprocess
import hashlib

HOST_DIR = r"C:\KnowTheMice\windows\publish_host"
SETUP_PROJ = r"C:\KnowTheMice\windows\KnowTheMice.Setup\KnowTheMice.Setup.csproj"
PAYLOAD_ZIP = r"C:\KnowTheMice\windows\KnowTheMice.Setup\payload.zip"
SETUP_PUBLISH_DIR = r"C:\KnowTheMice\windows\publish_setup"
DOTNET = r"C:\Users\gokul\.dotnet\dotnet.exe"

def build():
    if os.path.exists(PAYLOAD_ZIP):
        os.remove(PAYLOAD_ZIP)
    
    with zipfile.ZipFile(PAYLOAD_ZIP, 'w', zipfile.ZIP_DEFLATED) as zf:
        for root, dirs, files in os.walk(HOST_DIR):
            for f in files:
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, HOST_DIR)
                zf.write(full_path, rel_path)
    
    zip_size = os.path.getsize(PAYLOAD_ZIP)
    print(f"Created payload.zip: {zip_size} bytes")

    cmd = [
        DOTNET, "publish", SETUP_PROJ,
        "-c", "Release",
        "-r", "win-x64",
        "--self-contained", "false",
        "-p:PublishSingleFile=true",
        "-o", SETUP_PUBLISH_DIR
    ]
    subprocess.run(cmd, check=True)

    exe_path = os.path.join(SETUP_PUBLISH_DIR, "KnowTheMice-Setup-x64.exe")
    exe_size = os.path.getsize(exe_path)
    print(f"Built Setup Installer: {exe_path} ({exe_size} bytes)")

    with open(exe_path, "rb") as f:
        sha256 = hashlib.sha256(f.read()).hexdigest().upper()
    print(f"Setup SHA-256: {sha256}")

    web_bin_pub = r"C:\KnowTheMice\website\public\downloads\windows\KnowTheMice-Setup-x64.bin"
    web_bin_dist = r"C:\KnowTheMice\website\dist\downloads\windows\KnowTheMice-Setup-x64.bin"
    os.makedirs(os.path.dirname(web_bin_pub), exist_ok=True)
    os.makedirs(os.path.dirname(web_bin_dist), exist_ok=True)
    shutil.copy2(exe_path, web_bin_pub)
    shutil.copy2(exe_path, web_bin_dist)
    print("Copied installer to website downloads as .bin")

    return sha256, exe_size

if __name__ == "__main__":
    build()
