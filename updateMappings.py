#!/usr/bin/python
#######################################################
# Copyright 2020 Paul Becker @cheaterpaul
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
# to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#######################################################

#######################################################
#
#     gets all mappings from the specific version and replaces in all .java files in the src directory every searge/param name with the mapped name
#
#     default usage: ./updateMappings.py -s src/ -c snapshot -v 20200723-1.16.1 -mfp
#
#######################################################
import argparse
import csv
import io
import os
import signal
import sys
import zipfile

parser = argparse.ArgumentParser()
parser.add_argument("-s", "--src", required=True, help="src files directory")
parser.add_argument("-c", "--channel", required=True, help="mapping channel")
parser.add_argument("-v", "--version", required=True, help="mapping version ")
parser.add_argument("-m", "--methods", help="should method mappings be included", action='store_true')
parser.add_argument("-f", "--fields", help="should field mappings be included", action='store_true')
parser.add_argument("-p", "--params", help="should parameter mappings be included", action='store_true')
parser.add_argument("--mappingFile", help="custom mapping zip file (mappings and channel are ignored)")
args = parser.parse_args()

channel = args.channel

if args.mappingFile is None:
    mappingFile = os.path.expanduser(
        "~/.gradle/caches/forge_gradle/maven_downloader/de/oceanlabs/mcp/mcp_" + channel + "/" + args.version + "/mcp_" + channel + "-" + args.version + ".zip")
else:
    mappingFile = args.mappingFile

dict = {}
tmpfiles = []


def signal_handler(sig, frame):
    print('')
    print("Clean up")
    for file in tmpfiles:
        try:
            os.remove(file)
        except:
            pass

    sys.exit(0)


signal.signal(signal.SIGINT, signal_handler)


def addMappings(type, key="searge", value="name"):
    with zipfile.ZipFile(mappingFile) as zip:
        with zip.open(type, 'r') as csv_file:
            csv_file = io.TextIOWrapper(io.BytesIO(csv_file.read()))
            csv_reader = csv.DictReader(csv_file)
            line_count = 0
            for row in csv_reader:
                if line_count == 0:
                    line_count += 1
                else:
                    dict[row[key]] = row[value]
                    line_count += 1


def getFiles(dir):
    filelist = []
    for root, dirs, files in os.walk(dir):
        for file in files:
            if file.endswith('.java'):
                filelist.append(os.path.abspath(os.path.join(root, file)))
        for dir in dirs:
            filelist.extend(getFiles(dir))
    return filelist


fileNumber = 0
filelist = getFiles(args.src)


def replaceNames(fileName):
    global fileNumber
    with open(fileName, "rt") as file:
        tmpfile = fileName + ".new"
        with open(tmpfile, "wt") as file1:
            tmpfiles.append(tmpfile)
            replace = False
            for line in file:
                for sarge, name in dict.items():
                    if sarge in line:
                        replace = True
                        line = str.replace(line, sarge, name)
                file1.write(line)
            if replace:
                os.remove(fileName)
                os.renames(fileName + ".new", fileName)
            else:
                os.remove(fileName + ".new")
    fileNumber += 1
    print("\rFiles checked " + str(fileNumber) + "/" + str(len(filelist)), end='')


def replaceFiles(files):
    for file in files:
        replaceNames(file)


if args.methods:
    print("checking methods")
    addMappings("methods.csv")
if args.fields:
    print("checking fields")
    addMappings("fields.csv")
if args.params:
    print("checking parameter")
    addMappings("params.csv", "param")

if len(dict) == 0:
    print("No mappings to check, pls choose methods, fields, or parameter")
    exit(0)

print("\rFiles checked " + str(fileNumber) + "/" + str(len(filelist)), end='')
replaceFiles(filelist)
print('')
