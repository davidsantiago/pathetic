# pathetic

Simple unix-style path manipulation in Clojure.

This library provides a small number of functions meant to ease the manipulation of
file paths. For example, it provides functions to normalize (remove superfluous or
roundabout path components, like "." and ".."), relativize (transform one path to be
relative to another), and resolve (add path components to an existing path) paths.

Path handling is filled with all sorts of platform-specific behaviors and expectations.
Java 7 provides some APIs for dealing with paths in a cross-platform way, but even they
leave some behavior unimplemented, or defined as "platform-specific." Pathetic aims much
lower: simple unix-style paths. Windows or HFS paths aren't going to work here, this is
pretty much limited to the simplest things you might do on a unix system or a URL.
Anyhow, if you can't use Java 7 yet, those APIs won't help you much now.

## Usage

- `absolute-path? [path]`
Returns true if the given argument is an absolute path.

- `normalize [path]`
Cleans up a path so that it has no leading/trailing whitespace, and
removes any unremovable same-/parent-dir references.

- `relativize [base-path dest-path]`
Takes two absolute paths or two relative paths, and returns a relative path
that indicates the same file system location as destination-path, but
relative to base-path.

- `resolve [base-path other-path]`
Resolve the other-path against the base-path. If other-path is absolute,
the result is other-path. If other-path is nil, the result is base-path.
Otherwise, the result is other-path concatenated onto base-path. Does not
normalize its output.

## Obtaining

If you are using Cake or Leiningen, you can simply add 

    [pathetic "0.1.0"]

to your project.clj and download it from Clojars with 

    cake deps

or 

    lein deps

## License

Copyright (C) 2011 David Santiago

Distributed under the Eclipse Public License, the same as Clojure.
