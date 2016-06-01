# <<name|title>>

## Tooling

This project uses [boot](http://boot-clj.com/)
<img width="24px" height="24px" src="https://github.com/boot-clj/boot-clj.github.io/blob/master/assets/images/logos/boot-logo-3.png" alt="Boot Logo"/>
and could not be more happy.

All the customization happens in `build.boot` and (rarely) in `dev/boot.clj`. The core of `build.boot` is the `boot/options` multi-method, which allows the developer to specify different configurations based on the `[flavor build-type]` vector.
It follows a simple rule: the returned map will have to contain keys that match the boot task you want to configure.

Thence a map like
```
{:repl {:init-ns 'dev
        :port 5055}
 :jar {:main '<<name>>.core
       :file "<<name>>-standalone.jar"}
 :aot {:all true}}
```
will setup the `repl`, `jar` and `aot` boot built-in tasks respectively.

All of the boot commands accept a `-f|--flavor` and `-t|--type` that defines which option map the boot tasks will work against. Reasonable defaults are employed when these are missing, feel free to run `boot dev|build|test -h` for additional help.

### Interactive workflow

`boot dev` will launch:

 - A Clojure nRepl on port 5055, the backend server itself exposed on port 3000
<% if any frontend %> - A ClojureScript Repl on port 5088
<% endif %>
You can explore `env/dev/src/dev.clj` for getting acquainted with the system management tools. In there, `(dev/reset)` is the one that gives you the canonical reloaded workflow, featuring [mount](https://github.com/tolitius/mount).

<% if any frontend %>
There is also an option for using Dirac instead of a regular cljs repl: you first need to follow Dirac's [installation](https://github.com/binaryage/dirac/blob/master/docs/installation.md) instructions, then you can launch `boot dev -d` and open the [Dirac Chrome Extension](https://chrome.google.com/webstore/detail/dirac-devtools/kbkdngfljkchidcjpnfcgcokkbhlkogi) at [http://localhost:8000](http://localhost:8000).<% endif %>
### Build

For building the final artifact you need:

`boot build -t prod|dev target`

The artifact will be materialized in the `target` folder if you append the `target` task. Note that you *have to* specify a final task in order to dump the artifact but you can specify any task for deployment (e.g.: [`sync-bucket`](https://github.com/confetti-clj/confetti#syncing-your-site)). The `build` command defaults to `prod` when called with no arguments.

The backend, which will exit immediately as it is "empty", can be started with:

`java -jar <<project-ns>>-standalone.jar <<project-ns>>.core`
<% if any frontend %>
In case of backend *and* frontend, `boot build` does not actually build both cause we would lose boot's powerful task chaining. In that case, it is better to specify the flavor with `-f|--flavor` or to set the `BOOT_BUILD_FLAVOR` environment variable.<% endif %>

#### Logging

Logging is based on [clojure.tools.logging](https://github.com/clojure/tools.logging) and [Apache Log4j 2](https://logging.apache.org/log4j/2.x/).
If you need to control the configuration from a custom file you can launch the executable with:

`java -Dlog4j.configurationFile "your log4j2.xml path" -cp <<project-ns>>-standalone.jar <<project-ns>>.core`

Otherwise the provided configuration file is `env/prod|dev|test/resources/log4j2.xml`.

#### Config

The config files are in `env/dev/resources/config.edn` (mainly server-side) and `env/dev/src/<<sanitized>>/env.cljc` (potentially shared between Clojure and ClojureScript). You can see the merged content with `(dev/config)`:

```
{:greeting "<<name|title>>"
 :version "0.1.0-SNAPSHOT"
 :build :prod|:dev|:test
 :logging {:level :debug}
 ...}
```

Note that `cprop` will merge environment variables and system properties if and only if they are already present in `config.edn` or `env.cljc` with the right nesting *and* syntax.

<% if any frontend %>
#### Serve files

Sometimes it is useful to serve files from `target`, for instance to check if everything works fine before deploying. With `boot` you don't need no more `python -m SimpleHTTPServer`:

`boot serve -d target dev -f frontend target` (for interactive development)

`boot serve -d target build -f frontend -t prod target wait` (for static content only)

Check `boot serve -h` for the other options.
<% endif %>
#### Testing

Backend tests use `clojure.test` and can be triggered with:

`boot test -f backend` or `boot watch test -f backend` (for auto testing)
<% if any frontend %>
Frontend tests use `cljs.test`, [`boot-cljs-test`](https://github.com/crisptrutski/boot-cljs-test) and `doo` with PhantomJS configuration. First of all you need to download and install [PhantomJS](http://phantomjs.org/download.html). Then you can:

`boot test -f frontend` or `boot watch test -f frontend`
<% endif %>
More succintely, the shortcut `boot test` triggers all the tests in your project.

## Other Resources

 * [boot docs](https://github.com/boot-clj/boot/tree/master/doc)
 * [log4j2 configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html)
 * [cprop env vars syntax](https://github.com/tolitius/cprop#speaking-env-variables)
 * [cprop system prop syntax](https://github.com/tolitius/cprop#system-properties-cprop-syntax)
 * [doo environments](https://github.com/bensu/doo#setting-up-environments)
