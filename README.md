# cs7is3-team4

The group assignment of Team 4 for CS7IS3

## How to

Download the renamed corpora from [issues/2#issuecomment-600780315](https://github.com/tannineo/cs7is3-team4/issues/2#issuecomment-600780315). Extract the corpora as the `corpora` folder.

Build the project using the script `compile.sh`.

```text
$ sh compile.sh
```

A jar called `InfoSeekers.jar` should appear at the root of the project folder.

Run the compiled `InfoSeekers.jar` file.

```text
$ java -jar InfoSeekers.jar
```

## Implemetation

### Parsing

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

### Indexing

### Querying

## About

Most of the working process can be viewed by going through the [issues](https://github.com/tannineo/cs7is3-team4/issues) of the repository.
