#######################################################
# Copyright 2017 Max Becker @maxanier
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
#     Checks all .java files below the 'src' directory for language keys
#     and if they are present in the standard language file.
#
#######################################################
import os
import re

def readKeys(lines):
    keys = []
    for line in lines:
        if line.startswith('#'):
            continue
        parts=line.split('=')
        if parts and len(parts)==2:
            keys.append(parts[0])
    return keys


def checkSourceFile(filename,sourcelines, keys):
    missing = []
    for n,line in enumerate(sourcelines):

        line = re.sub(res_regex,"",line)
        matches = re.finditer(lang_key_regex, line)
        for match in matches:
            key=match.group(1)
            if key not in keys:
                print("Missing '",key,"' for '",filename,"' at line ",n)
                missing.append(key)
    return missing

def checkKeys(keys):
    for key in keys:
        if not re.match(lang_key_regex, '"'+key+'"'):
            if re.search(vampirism_regex, key) and key != "itemGroup.vampirism":
                print("Language key '",key,"' does not match expected format")
    pass


print("Checking for language keys without a string")

keys=[]
lang_key_regex=r'"(\w+\.vampirism\.[a-z\._\d]*[a-z\d])"'  # Capture all language keys style string. They have to end with a word character
vampirism_regex=r'vampirism'
res_regex=r'new ResourceLocation\("[a-z\._\d]+"\)'

with open('src/main/resources/assets/vampirism/lang/en_US.lang','r') as langfile:
    keys.extend(readKeys(langfile.readlines()))

with open('src/main/resources/assets/vampirismguide/lang/en_US.lang','r') as langfile:
    keys.extend(readKeys(langfile.readlines()))

checkKeys(keys)

missing = []


for root, dirs, filenames in os.walk('src'):
    for f in filenames:
        if f.endswith('.java'):
            with open(os.path.join(root,f), 'r') as sourcefile:
                missing.extend(checkSourceFile(os.path.join(root,f),sourcefile.readlines(),keys))

if len(missing) >0:
    print("WARNING: Missing strings for some language keys")
    exit(1)
else:
    print("All language keys found have a valid string")
    exit(0)


