import os
import shutil
import zipfile
import subprocess
import hashlib

HOST_PROJ = r"C:\KnowTheMice\windows\KnowTheMice.Host\KnowTheMice.Host.csproj"
HOST_DIR = r"C:\KnowTheMice\windows\publish_host"
SETUP_PROJ = r"C:\KnowTheMice\windows\KnowTheMice.Setup\KnowTheMice.Setup.csproj"
PAYLOAD_ZIP = r"C:\KnowTheMice\windows\KnowTheMice.Setup\payload.zip"
SETUP_PUBLISH_DIR = r"C:\KnowTheMice\windows\publish_setup"
WIX_PROJ = r"C:\KnowTheMice\windows\KnowTheMice.Installer\KnowTheMice.Installer.wixproj"
WIX_OUTPUT_MSI = r"C:\KnowTheMice\windows\KnowTheMice.Installer\bin\x64\Release\KnowTheMice-Setup-x64.msi"
DOTNET = r"C:\Users\gokul\.dotnet\dotnet.exe"

def build():
    # 1. Publish Host
    print("--- 1. Publishing Windows Host ---")
    os.makedirs(HOST_DIR, exist_ok=True)
    subprocess.run([DOTNET, "publish", HOST_PROJ, "-c", "Release", "-o", HOST_DIR], check=True)

    # 2. Package payload.zip
    print("--- 2. Packaging payload.zip ---")
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

    # 3. Publish Single-File Setup EXE
    print("--- 3. Publishing Single-File Setup EXE ---")
    os.makedirs(SETUP_PUBLISH_DIR, exist_ok=True)
    cmd_exe = [
        DOTNET, "publish", SETUP_PROJ,
        "-c", "Release",
        "-r", "win-x64",
        "--self-contained", "false",
        "-p:PublishSingleFile=true",
        "-o", SETUP_PUBLISH_DIR
    ]
    subprocess.run(cmd_exe, check=True)

    exe_path = os.path.join(SETUP_PUBLISH_DIR, "KnowTheMice-Setup-x64.exe")
    exe_size = os.path.getsize(exe_path)
    with open(exe_path, "rb") as f:
        exe_sha256 = hashlib.sha256(f.read()).hexdigest().upper()
    print(f"Built Setup Installer EXE: {exe_path} ({exe_size} bytes)")
    print(f"EXE SHA-256: {exe_sha256}")

    # 4. Build WiX MSI Package
    print("--- 4. Building WiX MSI Package ---")
    cmd_msi = [
        DOTNET, "build", WIX_PROJ,
        "-c", "Release"
    ]
    subprocess.run(cmd_msi, check=True)

    msi_dest = os.path.join(SETUP_PUBLISH_DIR, "KnowTheMice-Setup-x64.msi")
    shutil.copy2(WIX_OUTPUT_MSI, msi_dest)
    msi_size = os.path.getsize(msi_dest)
    with open(msi_dest, "rb") as f:
        msi_sha256 = hashlib.sha256(f.read()).hexdigest().upper()
    print(f"Built Setup Installer MSI: {msi_dest} ({msi_size} bytes)")
    print(f"MSI SHA-256: {msi_sha256}")

    # 5. Copy to Website Distribution
    print("--- 5. Staging to Website Downloads ---")
    web_win_pub = r"C:\KnowTheMice\website\public\downloads\windows"
    web_win_dist = r"C:\KnowTheMice\website\dist\downloads\windows"
    os.makedirs(web_win_pub, exist_ok=True)
    os.makedirs(web_win_dist, exist_ok=True)

    # EXE
    shutil.copy2(exe_path, os.path.join(web_win_pub, "KnowTheMice-Setup-x64.bin"))
    shutil.copy2(exe_path, os.path.join(web_win_dist, "KnowTheMice-Setup-x64.bin"))
    # MSI
    shutil.copy2(msi_dest, os.path.join(web_win_pub, "KnowTheMice-Setup-x64.msi.bin"))
    shutil.copy2(msi_dest, os.path.join(web_win_dist, "KnowTheMice-Setup-x64.msi.bin"))

    print("Successfully copied Windows EXE and MSI packages to website downloads!")
    return {
        "exe": {"path": exe_path, "size": exe_size, "sha256": exe_sha256},
        "msi": {"path": msi_dest, "size": msi_size, "sha256": msi_sha256}
    }

if __name__ == "__main__":
    build()
