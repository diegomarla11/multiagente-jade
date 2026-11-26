"""Instala los dos JAR de la entrega a partir de las distribuciones públicas."""
import hashlib
from io import BytesIO
from pathlib import Path
import sys
from tempfile import NamedTemporaryFile
from urllib.error import URLError
from urllib.request import urlopen
from zipfile import BadZipFile, ZipFile

LIB = Path(__file__).resolve().parent / "lib"
JADE_URLS = (
    "https://jade.tilab.com/download/JADE-bin-4.5.0.zip",
    "https://gitlab.com/jade-project/jade/-/raw/master/archive/Jade-all-4.5.0/JADE-bin-4.5.0.zip",
)
JSON_URL = "https://repo.maven.apache.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar"
PACKAGES = (
    ("jade.jar", "908499cac16e94b02856a71e939b72735015e91715c247528da26b1e71f4075f", JADE_URLS),
    ("json-simple-1.1.1.jar", "108045c9675ebf6d6ec4d8dcc331693f9b944ac7c87de52dccff2c55e8dfa6f1", (JSON_URL,)),
)


def install(name, digest, urls):
    target = LIB / name
    if target.is_file() and hashlib.sha256(target.read_bytes()).hexdigest() == digest:
        return
    for url in urls:
        try:
            with urlopen(url, timeout=15) as response:
                data = response.read()
            if url.endswith(".zip"):
                with ZipFile(BytesIO(data)) as archive:
                    entry = next(path for path in archive.namelist() if path.endswith("/lib/jade.jar"))
                    data = archive.read(entry)
            if hashlib.sha256(data).hexdigest() != digest:
                raise ValueError("el SHA-256 difiere del JAR original")
            with NamedTemporaryFile(dir=LIB, delete=False) as tmp:
                tmp.write(data)
            Path(tmp.name).replace(target)
            return
        except (OSError, URLError, BadZipFile, StopIteration, ValueError) as error:
            print(f"Descarga de {name}: {error}", file=sys.stderr)
    sys.exit(f"Coloca {name} en lib/ como se indica en el README y vuelve a ejecutar este comando.")


if __name__ == "__main__":
    LIB.mkdir(exist_ok=True)
    for package in PACKAGES:
        install(*package)
    print("Dependencias verificadas.")
