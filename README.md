# Scalarchiver

A Scala archiver program to archive under different compression codecs. The output are chunked files of a given size

Currently using Zip but easily extendable to GZip or LZip

## How to build

Like any [SBT](http://www.scala-sbt.org/) project, please see [here](http://www.scala-sbt.org/0.13/docs/Hello.html)

## Usage

```
> scala scalarchiver.jar --help
Scalarchiver 0.1.0
Usage: scalarchiver [compress|extract] [options]

  -i, --in <file>          Path to Input directory
  -o, --out <file>         Path to Output directory
Command: compress [options]
Create a new archive containing the specified items.
  -s, --chunkSize <MB>     Maximum compressed size per file expressed in MB
Command: extract
Extract to disk from the archive.
  --version                Print version information and quit
  --help                   prints this usage text and quit
  
```

## Examples

### Compress

Will compress all the files and directory present in `/path/to/input` to `/path/to/output` and will generate 
some files like `archive.part.0`, `archive.part.1` ... `archive.part.n` where which chunk will have a size of `2MB`

```
> scala scalarchiver.jar compress -i /path/to/input -o /path/to/output -s 2
```

### Extract

Will take the chunk files from `/path/to/input` an extract their content to `/path/to/output`

```
> scala scalarchiver.jar extract -i /path/to/input -o /path/to/output
```

## TODO

 - Improve exception management
 - Add some unit tests
 - Make the compression process run in parallel
