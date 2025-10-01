import os
import sys
import subprocess
import venv

VENV_DIR = ".venv"
REQ_FILE = "requirements.txt"


def create_venv():
    if not os.path.exists(VENV_DIR):
        print(f"📦 Creating virtual environment in {VENV_DIR} ...")
        venv.create(VENV_DIR, with_pip=True)
    else:
        print(f"✅ Virtual environment already exists in {VENV_DIR}")


def install_requirements():
    if not os.path.exists(REQ_FILE):
        print(f"⚠️  {REQ_FILE} not found")
        return

    pip_executable = os.path.join(VENV_DIR, "bin", "pip")
    if not os.path.exists(pip_executable):
        print("⚠️ pip not found in virtual environment. Did venv creation fail?")
        return

    print(f"📥 Installing requirements from {REQ_FILE} ...")
    subprocess.check_call([pip_executable, "install", "-r", REQ_FILE])


if __name__ == "__main__":
    create_venv()
    install_requirements()

    activate_path = os.path.join(VENV_DIR, "bin", "activate")
    print("\n✨ Setup complete!")
    print(f"➡️ To activate your environment, run:\n\n   source {activate_path}\n")
