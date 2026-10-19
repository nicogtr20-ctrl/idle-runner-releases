#!/usr/bin/env python3
"""Read-only V357 motor audit; compile ACTUAL repository code without APK/OTA."""
import pathlib, re, shlex, subprocess, tempfile
root=pathlib.Path(__file__).resolve().parents[1]
text=(root/'.github/workflows/overmind-ota-publish.yml').read_text()
match=re.search(r'^\s*(javac -d /tmp/overmind [^\n]+)',text,re.M)
assert match,'canonical real-world Java dependencies missing'
files=shlex.split(match.group(1))[3:]
java=root/'app/src/main/java/com/overmind/zero'
files += [str(x.relative_to(root)) for x in java.glob('Zero3*.java')
          if not any(name in x.name for name in ('AndroidSession','Activity','AtlasView','OriginalBrain'))]
files+=['tools/qa/Zero364OldPeripheralFourWay.java']
with tempfile.TemporaryDirectory(prefix='v358-physical-motor-audit-') as scratch:
    dst=pathlib.Path(scratch)
    subprocess.run(['java','--module','jdk.compiler/com.sun.tools.javac.Main',
                    '-d',str(dst),*dict.fromkeys(files)],cwd=root,check=True)
    subprocess.run(['java','-Xmx1024m','-cp',str(dst),
                    'com.overmind.zero.Zero364OldPeripheralFourWay'],
                   cwd=root,check=True,timeout=180)
