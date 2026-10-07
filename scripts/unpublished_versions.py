#!/usr/bin/env python3
"""Print, as a JSON array, every iso-codes release not yet published to Maven Central.

Upstream releases come from the tags of https://salsa.debian.org/iso-codes-team/iso-codes.
Releases before 3.67 are skipped: 3.66 used a different JSON layout and older ones had no JSON at all.
"""
import json
import re
import sys
import urllib.error
import urllib.request
from pathlib import Path

TAGS_URL = "https://salsa.debian.org/api/v4/projects/iso-codes-team%2Fiso-codes/repository/tags?per_page=100&page={}"
OLDEST = (3, 67)
ARTIFACT = "iso-codes"


def gradle_property(name):
    text = (Path(__file__).resolve().parent.parent / "gradle.properties").read_text()
    match = re.search(rf"^{re.escape(name)}=(.*)$", text, re.MULTILINE)
    return match.group(1).strip()


def upstream_versions():
    versions, page = [], 1
    while True:
        with urllib.request.urlopen(TAGS_URL.format(page)) as response:
            tags = json.load(response)
        if not tags:
            break
        for tag in tags:
            match = re.fullmatch(r"(?:v|iso-codes-)(\d+(?:\.\d+)+)", tag["name"])
            if match and tuple(map(int, match.group(1).split("."))) >= OLDEST:
                versions.append(match.group(1))
        page += 1
    return sorted(set(versions), key=lambda v: tuple(map(int, v.split("."))))


def published_versions(group):
    url = f"https://repo1.maven.org/maven2/{group.replace('.', '/')}/{ARTIFACT}/maven-metadata.xml"
    try:
        with urllib.request.urlopen(url) as response:
            return set(re.findall(r"<version>([^<]+)</version>", response.read().decode()))
    except urllib.error.HTTPError as e:
        if e.code == 404:
            return set()
        raise


def main():
    group = gradle_property("group")
    published = published_versions(group)
    missing = [v for v in upstream_versions() if v not in published]
    print(json.dumps(missing))
    print(f"{len(missing)} unpublished release(s) for {group}:{ARTIFACT}", file=sys.stderr)


if __name__ == "__main__":
    main()
