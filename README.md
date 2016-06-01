# Lambone

Lambone (/lamˈbone/) is an project template for bare backend + frontend projects.

It proudly features [boot](http://boot-clj.com/) <img width="24px"
height="24px"
src="https://github.com/boot-clj/boot-clj.github.io/blob/master/assets/images/logos/boot-logo-3.png"
alt="Boot Logo"/> and does not at the moment generate a Leiningen `project.clj`.

The backend is materialized by default, with `+frontend` enabling both. This
division is present throughout the project, from boot dependencies
(`backend-deps` vs `frontend-deps` in `build.boot`) to source files
organization (`src/backend` vs `src/frontend`).

It is bare in the sense that it does not include anything more than:

* Backend
 * [mount](https://github.com/tolitius/mount)
 * [cprop](https://github.com/tolitius/cprop)
 * [log4j2](https://logging.apache.org/log4j/2.x/manual/index.html)
 
* Frontend
 * [clojurescript](https://github.com/clojure/clojurescript)
 * [cljs-console](https://github.com/adzerk-oss/cljs-console)
 * [sass4clj](https://github.com/Deraen/sass4clj)
 * [dirac](https://github.com/binaryage/dirac)

Why would you want to use `lambone` then?

This template does not want by any means compete against Luminus, which the [current maintainer](https://github.com/arichiardi) is actually trying to [enhance](https://github.com/luminus-framework/luminus-template/issues/223) with boot support, but it does provide a couple of neat features that you can setup by yourself but are a bit cumbersome to remember every time you bootstrap a project:

* Three ready to use `boot build`, `boot dev` and `boot test` tasks
* `boot dev` with no options potentially launches two Repl instances within the same JVM
* Easy to configure, flavor-like `boot` task configuration through multi-methods and `-f|--flavor` option
* Consistent hooks for taming Java logging and redirect all of it to log4j2
* Canonical reload workflow and browser refreshing
* Ready to use test harness (you only need to have PhantomJS on the target machine)
* Switch from a Clojurescript Repl to a [Dirac](https://github.com/binaryage/dirac) Repl at will (`-d/--dirac` option to the `dev` task)

## Usage

Very simply:

`lein new lambone <project-name>`

Or:

`lein new lambone <project-name> +frontend`

While `boot-new` users can:

`boot -d seancorfield/boot-new new -t lambone -n <project-name>`

Or:

`boot -d seancorfield/boot-new new -t lambone -n <project-name> -a +frontend`

## Thanks

The authors and contributors of the following paved the way magnificently:

* [Luminus](https://github.com/luminus-framework/luminus-template)
* [Edge](https://github.com/juxt/edge)
* [System](https://github.com/danielsz/system)
  
## License

Copyright © 2016 Scalac Sp. z o.o.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
