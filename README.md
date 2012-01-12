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

- `normalize [path],[path sep]`
Cleans up a path so that it has no leading/trailing whitespace, and
removes any unremovable same-/parent-dir references. If given, sep is a string containing
the path separator to use.

- `relativize [base-path dest-path],[base-path dest-path sep]`
Takes two absolute paths or two relative paths, and returns a relative path
that indicates the same file system location as destination-path, but
relative to base-path. If given, sep is a string containing the path separator to use.

- `resolve [base-path other-path],[base-path other-path sep]`
Resolve the other-path against the base-path. If other-path is absolute,
the result is other-path. If other-path is nil, the result is base-path.
Otherwise, the result is other-path concatenated onto base-path. Does not
normalize its output. If given, sep is a string containing the path separator to use.

- `ensure-trailing-separator [path],[path sep]`
If the path given does not have a trailing separator, returns a new path that has one.
Otherwise, returns the original path. If given, sep is a string containing the path
separator to use.

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

- Released version 0.3.0
  - Most core functions now accept an additional, optional path-separator argument.
  - Addition of ensure-trailing-separator function.
  - Addition of URL utility functions `split-url-on-path`, `url-normalize`, and `url-ensure-trailing-separator`.

- Released version 0.2.0
  - All core functions now accept File arguments as well as strings. (Actually, anything that
    will give a path when it has `str` called on it).

## Obtaining

If you are using Cake or Leiningen, you can simply add 

    [pathetic "0.3.0"]

to your project.clj and download it from Clojars with 

    lein deps

## License

Copyright (C) 2011 David Santiago

Distributed under the Eclipse Public License, the same as Clojure.
