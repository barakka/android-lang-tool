android-lang-tool
=================

Tool for exporting and importing Android string resources for translation.

Supported resources:
* strings
* string arrays
* plurals

It's [AndroidLangTool](https://github.com/hamsterksu/AndroidLangTool) on steroids.

The tool exports Android string resources to Excel and imports them back to the project after translation.
It scans Android project and exports strings, by default from strings.xml. Additional resources can be specified.
All the resources are concatenated in a single Excel file.
The tool allows many additional operations on the strings. See the command line arguments for more details.

Xml comments are NOT supported 
Missing traslations have red background in the xls file.

To build the application execute: `mvn package`
To run the application execute: `java -jar langtools-VERSION-jar-with-dependencies.jar`

Tool has 2 modes:
* exporting to xls
* importing from xls
 
## Exporting
```
params: -e <project dir> 
    [-o <output file>] 
    [--additional-resources <list of additional resources>]
    [--ignore-list <ingored list file>] 
```

* **project dir** - Path to the Android project 
* **output file** - Name of the generated Excel file
* **list of additional resources** - Optional list of additional resources, values are separated by ':'
* **ingored list file** - Optional file for defining keys that are ignored. NOT SUPPORTED AT THE MOMENT.

## Importing

```
params: -i <input file> 
    [-m <mapping file>] 
    [--escaping-config <escaping config file>] 
    [--ignore-list <ingored list file>] 
    [--extra-transformations <transformations config file>]
    [--mixed-content <mixed list file>]
```

* **input file** - Name of the Excel file for importing into the project
* **mapping file** - Optional file for changing resource qualifiers onto another. Typically used for omitting country 
specifiers (e.g. convert 'cs-rCZ' into 'cs'). NOT SUPPORTED AT THE MOMENT.
* **escaping config file** - Optional file for defining string keys that should be escaped (with quotes) in the final 
output. NOT SUPPORTED AT THE MOMENT.
* **ingored list file** - Optional file for defining string keys that are ignored. NOT SUPPORTED AT THE MOMENT.
* **transformations config file** - Optional file for defining import tranformations on strings for each key. NOT SUPPORTED AT THE MOMENT.
* **mixed list file** - Optional file containing keys of string which will be handled as mixed xml content when importing (strings are by default handled as text content). NOT SUPPORTED AT THE MOMENT.

#### Format of mapping file (NOT SUPPORTED AT THE MOMENT)

* The first column contains 'from value'
* The second column contains 'to value'

#### Format of escaping config file (NOT SUPPORTED AT THE MOMENT)

* The first column contains string keys values of which will be escaped with quotes

#### Format of ingored list file (NOT SUPPORTED AT THE MOMENT)

* The first column contains string keys

#### Format of transformations config file (NOT SUPPORTED AT THE MOMENT)

* The first column contains string keys
* The second column contains matching regex (can contain capture groups) as defined by Java Pattern documentation
* The third column contains replacement (can contain capture groups)
* The optional fourth column can constain comma separated list of allowed languages (the transformation is applied 
only for these languages).

#### Format of mixed list file (NOT SUPPORTED AT THE MOMENT)

* The first column contains string keys
