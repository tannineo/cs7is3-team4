# cs7is3-team4

The group assignment of Team 4 for CS7IS3

- [cs7is3-team4](#cs7is3-team4)
  - [Quick Result](#quick-result)
  - [How to](#how-to)
  - [Implemetation](#implemetation)
    - [Parsing](#parsing)
      - [Troubleshooting](#troubleshooting)
    - [Analyzer and Scorer Chosed](#analyzer-and-scorer-chosed)
  - [About](#about)

## Quick Result

Quick start with command below.

```text
$ java -jar -Xms4g -Xmx6g InfoSeekers.jar LMJelinekMercer Custom3
$ java -jar -Xms4g -Xmx6g InfoSeekers.jar BM25 Custom3
```

Or check the prepared running result (even quicker!).

## How to

Download the renamed corpora from [issues/2#issuecomment-600780315](https://github.com/tannineo/cs7is3-team4/issues/2#issuecomment-600780315). Extract the corpora as the `corpora` folder.

the folders should be organized like this:

```text
$ ls
corpora        cs7is3-team4
```

Build the project using the script `compile.sh`.

```text
$ cd cs7is3-team4
$ sh compile.sh
```

A jar called `InfoSeekers.jar` should appear at the root of the project folder.

Run the compiled `InfoSeekers.jar` file.

```text
$ java -jar -Xms4g -Xmx6g InfoSeekers.jar SCORER ANALYZER
```

Avalable scorers are:

- Classic
- BM25
- LMDirichlet
- LMJelinekMercer

Available analyzers are:

- Standard
- English
- Custom1
- Custom2
- Custom3

## Implemetation

### Parsing

2 approaches to parse the corpora, here is the complex one `:)` :

- `jsoup`: to directly parse sgml (used in java code)
- `sgml => xml => json`: the way is listed below (not recommended for high cost of time)

Using external tools:

- `osx` from OpenSP: An SGML System Conforming to International Standard ISO 8879 -- Standard Generalized Markup Language
  - http://openjade.sourceforge.net/doc/index.htm
- `hay/xml2json`: Python script converts XML to JSON or the other way around
  - https://github.com/hay/xml2json
  - with default python (2.7)

Steps:

1. `sh awk_split.sh` to copy and split files (1 file 1 DOC, it will generate many files end with `.split`)
2. `sh sgm2json.sh` to transform `.split` files into `.json` files
3. load `*.json` files to index

#### Troubleshooting

Entities: `amp`, `sect`, `hyph`, etc....

Mainly happens in `fr94`, TODO

And also `&hyph;` is not a standard entity (should be `&hyphen;` or `&dash;`, see [charref from w3c](https://dev.w3.org/html5/html-author/charref)).

We may need our own parser to parse entities.

---

`/bin/rm: cannot execute [Argument list too long]` when cleaning the generated files, solution:

```text
$ find ./corpora -name "*.sgmn*" -print0 | xargs -0 rm

Or use clean.sh
```

Too many files to cp/mv:

```text
$ find ../corpora/fbis -name "*.json" -exec cp -- "{}" ./fbis/ \;
```

---

One special file: `./corpora/fr94/fr940328_2.sgm` will cause `awk_split.sh` to throw an error.

Delete the first line of this file to make sure the file starts with `<DOC>`

### Analyzer and Scorer Chosed

There are 3 custom analyzers implemented:

- [MyAnalyzer](src/main/java/life/tannineo/cs7is3/group4/MyAnalyzer.java)
- [MySynonymAnalyzer](src/main/java/life/tannineo/cs7is3/group4/MySynonymAnalyzer.java)
- [CustomAnalyzer_Syn_stp](src/main/java/life/tannineo/cs7is3/group4/CustomAnalyzer_Syn_stp.java)

The [CustomAnalyzer_Syn_stp](src/main/java/life/tannineo/cs7is3/group4/CustomAnalyzer_Syn_stp.java) with `BM25`, `LMJelinekMercer` scorer produces the best result.

## About

Most of the working process can be viewed by going through the [issues](https://github.com/tannineo/cs7is3-team4/issues) of the repository.
