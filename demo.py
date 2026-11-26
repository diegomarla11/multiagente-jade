"""Compile and run the JADE portfolio demo with its pinned dependencies."""
import argparse
import os
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parent
BUILD = ROOT / "build"
LIB = ROOT / "lib"

def main():
    parser = argparse.ArgumentParser(description="Demo local de agentes JADE")
    parser.add_argument("--interactive", action="store_true", help="responder al cuestionario")
    parser.add_argument("--compile-only", action="store_true", help="solo compilar")
    args = parser.parse_args()
    for name in ("jade.jar", "json-simple-1.1.1.jar"):
        if not (LIB / name).is_file():
            sys.exit(f"Falta lib/{name}. Consulta el apartado «Dependencias» del README.")

    BUILD.mkdir(exist_ok=True)
    sources = sorted(str(path) for path in (ROOT / "src").rglob("*.java"))
    subprocess.run(["javac", "--release", "11", "-encoding", "UTF-8", "-cp", str(LIB / "*"),
                    "-d", str(BUILD), *sources], check=True)
    if not args.compile_only:
        classpath = os.pathsep.join([str(BUILD), str(LIB / "*")])
        mode = "interactive" if args.interactive else "demo"
        subprocess.run(["java", "-Djava.awt.headless=true", "-cp", classpath, "demo.Main", mode], cwd=BUILD, check=True)

if __name__ == "__main__":
    main()
