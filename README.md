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

Most of the work in this library is done by parsing a path into a
vector representation, which is referred to in the code and
documentation as a "path vector." If a path vector's first element is
`:root`, then the path is absolute. If the path vector represents a
relative path, then the first element is `:cwd`. 

You can turn a string containing a path into a path vector using the
function `parse-path`, and turn a path vector back into a string using
the function `render-path`. You could also conceivably generate your own
path vectors from some other file system and use the functions that work
on path vectors.

Note that nothing in pathetic looks at the actual file-system! All
logic is based on the semantics of paths, and unrelated to whether any
files actually exist or not.

- `absolute-path? [path]`
Returns true if the given string argument is an absolute path.

- `up-dir [path-vector]` 
Returns a new path vector that has gone up one
directory (as if ".." was the last component of the path).

- `normalize* [path-vector]`
Cleans up a path vector so that it has no removable same-/parent-dir references.

- `normalize [path]`
Cleans up a path so that it has no leading/trailing whitespace, and
removes any removable same-/parent-dir references. 

- `relativize* [base-path-vec dest-path-vec]`
Takes two absolute path vectors or two relative path vectors, and returns a relative path
vector that indicates the same file system location as the destination path, but relative
to the base path.

- `relativize [base-path dest-path]`
Takes two absolute paths or two relative paths, and returns a relative path
that indicates the same file system location as destination-path, but
relative to base-path. 

- `resolve* [base-path-vec other-path-vec]` 
Resolve the "other" path vector against the "base" path vector. If the "other"
path vector is absolute, the result is the "other" path vector. If the "other"
path is empty/nil, the result is the "base" path vector.

- `resolve [base-path other-path]`
Resolve the other-path against the base-path. If other-path is absolute,
the result is other-path. If other-path is nil, the result is base-path.
Otherwise, the result is other-path concatenated onto base-path. Does not
normalize its output. 

- `ensure-trailing-separator [path]`
If the path given does not have a trailing separator, returns a new path that has one.
Otherwise, returns the original path.

- `split-url-on-path [url-or-string]`
Given a URL or string containing a URL, returns a vector of the three component strings: the
stuff before the path, the path, and the stuff after the path.

- `url-normalize [url-or-string]`
Behaves like normalize on the path part of a URL, but takes a j.n.URL or
string containing a URL, and returns a string containing the same URL
instead of just a path. Everything but the path part of the URL is unchanged
(query, anchor, protocol, etc).

- `url-ensure-trailing-separator [url-or-string]`
Behaves like ensure-trailing-separator on the path part of a URL, but takes
a j.n.URL or string containing a URL, and returns a string containing the
same URL instead of just a path. Everything but the path part of the URL is
unchanged (query, anchor, protocol, etc).
   
## News

- Released version 0.5.0
  - Now works on both Clojure and ClojureScript, thanks to the work of [Julien Eluard](https://github.com/jeluard).
  - Now requires Clojure 1.4 or newer, to enable ClojureScript interop. This version contains no new
    functionality or bug fixes relative to 0.4.0, so that is still usable with earlier versions of Clojure.

- Released version 0.4.0
  - The separator character is no longer configurable, it is assumed to be "/". The associated arities have been removed. 
  - The use of java.io.File has been removed. Should work better on Windows now.

- Released version 0.3.1
  - Added lower-level functions `normalize*`, `relativize*`, and
    `resolve*`, which take path vectors and return path
    vectors. `normalize`, `relativize`, and `resolve` have been
    refactored to use these versions. As a result, the API is more
    flexible and the code should run a small bit faster due to savings
    on reparsing.

- Released version 0.3.0
  - Most core functions now accept an additional, optional path-separator argument.
  - Addition of ensure-trailing-separator function.
  - Addition of URL utility functions `split-url-on-path`, `url-normalize`, and `url-ensure-trailing-separator`.

- Released version 0.2.0
  - All core functions now accept File arguments as well as strings. (Actually, anything that
    will give a path when it has `str` called on it).

## Obtaining

If you are using Leiningen, you can simply add 

    [pathetic "0.5.0"]

to your project.clj.

## License

Copyright (C) 2011 David Santiago

Distributed under the Eclipse Public License, the same as Clojure.
