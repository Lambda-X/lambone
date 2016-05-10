# lambone

Lambone is an project template for bare backend + frontend projects.

It proudly features [boot](http://boot-clj.com/) <img width="24px"
height="24px"
src="https://github.com/boot-clj/boot-clj.github.io/blob/master/assets/images/logos/boot-logo-3.png"
alt="Boot Logo"/> and does not at the moment provide a Leiningen `project.clj`.

The backend is materialized by default, with `+frontend` enabling both. This
division is present throughout the project, from boot dependencies
(`backend-deps` vs `frontend-deps` in `build.boot`) to source files
organization (`src/backend` vs `src/frontend`).

It is bare in the sense that it does not include any anything more then:

* Backend
 * [mount](https://github.com/tolitius/mount)
 * [cprop](https://github.com/tolitius/cprop)

* Frontend
 * [clojurescript](https://github.com/clojure/clojurescript)
 * [cljs-console](https://github.com/adzerk-oss/cljs-console)
 * [sass4clj](https://github.com/Deraen/sass4clj)
 
* Log4j2
 * For a list of advantages: https://logging.apache.org/log4j/2.x/manual/index.html
 
Why would you want to use lambone then? The answer is that it does provide a couple
of neat features that you can setup by yourself but are a bit cumbersome to
remember every time:

* Three ready to use `boot build`, `boot dev` and `boot test` tasks
* `boot dev` with no options potentially launches two Repl instances within the same JVM
* Easy to configure, flavor-like `boot` task configuration through multi-methods and `-f|--flavor` option


 
## Usage

Very simply, for backend-only project:

`lein new lambone <you-app-name>`

Or:

`lein new lambone <you-app-name> +frontend`

For `boot-new` users

`boot -d seancorfield/boot-new new -t lambone -n test-arsarsars`

Or:

`boot -d seancorfield/boot-new new -t lambone -n test-arsarsars -a +frontend`

## License

Copyright © 2016 Scalac Sp. z o.o.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
