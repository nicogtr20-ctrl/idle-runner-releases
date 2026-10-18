#!/usr/bin/env python3
"""Compile actual ZERO3 JVM sources and run relevant physical/causal regressions.
No signed publisher, deployment or legacy profile access.
"""
import argparse
import pathlib
import re
import shlex
import subprocess
import tempfile

ROOT = pathlib.Path(__file__).resolve().parents[1]
JAVA = ROOT / 'app/src/main/java/com/overmind/zero'


def run(*args):
    subprocess.run([str(arg) for arg in args], cwd=ROOT, check=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--variable-clock', action='store_true')
    parser.add_argument('--cognitive', action='store_true')
    parser.add_argument('--long-life', type=int, default=2000)
    args = parser.parse_args()
    if args.long_life < 2000 or args.long_life > 10000:
        parser.error('life length must be in [2000, 10000]')
    with tempfile.TemporaryDirectory(prefix='zero3-qa-') as temporary:
        directory = pathlib.Path(temporary)
        classes = directory / 'classes'
        classes.mkdir()
        publisher = (ROOT / '.github/workflows/overmind-ota-publish.yml').read_text()
        match = re.search(r'^\s*(javac -d /tmp/overmind [^\n]+)', publisher, re.M)
        if match is None:
            raise RuntimeError('canonical real physics compilation list missing')
        files = shlex.split(match.group(1))[3:]
        files += [str(p.relative_to(ROOT)) for p in sorted(JAVA.glob('Zero3*.java'))
                  if 'AndroidSession' not in p.name and 'LabActivity' not in p.name]
        run('java', '--module', 'jdk.compiler/com.sun.tools.javac.Main',
            '-d', classes, *dict.fromkeys(files))
        for name in ('OpticalCues', 'Zero3RecurrentBrainRegression',
                     'Zero3RecurrentBrainCheckpointV345Regression',
                     'Zero3DurationCreditV350Regression',
                     'Zero3GruWholeLifeV348Regression',
                     'Zero3UnifiedSensoryV351Regression', 'Zero3VariableClockV352Regression',
                     'Zero3CognitionV353Regression'):
            run('java', '-Xmx1536m', '-cp', classes, 'com.overmind.zero.' + name)
        options = ['java', '-Xmx1536m', '-Dv328.gru=true', '-Dv351.unified=true',
                   '-Dv352.variable=' + str(args.variable_clock).lower(),
                   '-Dv353.cognitive=' + str(args.cognitive).lower(),
                   f'-Dv328.target={args.long_life}', '-Dv328.ordinal=61',
                   '-cp', classes, 'com.overmind.zero.Zero3LongLifeV328Regression']
        archive = directory / 'life'
        resumed, reference = directory / 'resumed.txt', directory / 'reference.txt'
        run(*options, 'prefix', archive)
        run(*options, 'resume', archive, resumed)
        run(*options, 'reference', reference)
        if resumed.read_bytes() != reference.read_bytes():
            raise AssertionError('full organism/replay changed after cold JVM restart')
        run(*options, 'fault', archive)
        label = 'V353' if args.cognitive else ('V352' if args.variable_clock else 'V351')
        print(f'{label} REAL_GRU194_{args.long_life}_SAME_LIFE_COLD_RESTART=PASS', flush=True)


if __name__ == '__main__':
    main()
