# <<name|title>>

## Tooling

This project uses [boot](http://boot-clj.com/)
<img width="24px" height="24px" src="https://github.com/boot-clj/boot-clj.github.io/blob/master/assets/images/logos/boot-logo-3.png" alt="Boot Logo"/>
and could not be more happy.

All the customization happen in `build.boot` and (rarely) in `dev/boot.clj`. The core of `build.boot` is the `boot/options` multi-method, which allows the developer to specify different configurations based on the `[flavor build-type]` vector.
The option format follows a simple rule: the first key has to match the task you want to configure.

So a map like:

```
{:repl {:init-ns 'dev
        :port 5055}
 :jar {:main '<<name>>.core
       :file "<<name>>-standalone.jar"}
 :aot {:all true}}
```

Will pass the options to the `repl`, `jar` and `aot` boot built-in tasks for instance.

Therefore, all of the boot commands accept a `-f|--flavor` and `-t|--type` that will define which option map the boot tasks will work against. Reasonable defaults are employed when these are missing.

### Interactive workflow

`boot dev` will launch:

 - The Clojure nRepl on port 5055
<% if any frontend %> - The ClojureScript Repl on port 5088 (execute `((eval 'adzerk.boot-cljs-repl/start-repl))` and connect to [http://localhost:3000](http://localhost:3000) for seeing `cljs.user`)
<% endif %>
Explore `env/dev/src/dev.clj` for getting acquainted with the system management tools. In there, `(dev/reset)` is the one that gives you the canonical reloaded workflow, featuring [mount](https://github.com/tolitius/mount).

### Build

For building the final artifact you need:

`boot build -t prod|dev target`

The artifact will be materialized in the `target` folder if you append the `target` task. Note that you *have to* specify a final task in order to dump the artifact but you can specify any task for deployment. The `build` command defaults to `prod` when called with no arguments.

#### Config

The config files are in `env/dev/resources/config.edn` (mainly server-side) and `env/dev/src/<<sanitized>>/env.cljc` (potentially shared between Clojure and ClojureScript). You can see the merged content with `(dev/config)`:

```
{:greeting "<<name|title>>"
 :version "0.1.0-SNAPSHOT"
 :build :prod|:dev|:test
 :logging {:level :debug}
 ...}
```
<% if any frontend %>
#### Serve files

Sometimes it is useful to serve files from `target`, for instance to check if everything works fine before deploying. With `boot` you don't need no more `python -m SimpleHTTPServer`:

`boot serve -d target dev -f frontend target` (for interactive development)

`boot serve -d target build -f frontend -t prod target wait` (for static content only)

Check `boot serve -h` for the other options.
<% endif %>
## Testing

Clojure tests use `clojure.test` and can be triggered with:

`boot test -f backend` or `boot watch test -f backend` (auto test)
<% if any frontend %>
ClojureScript tests use `cljs.test` and PhantomJS. First of all you need to download and install [PhantomJS](https://github.com/ariya/phantomjs/). Then you can:

`boot test -f frontend` or `boot watch test -f frontend` (auto test)
<% endif %>
More succintely, the shortcut `boot test` triggers all the tests in your project.

## Other Resources

 * [Boot docs](https://github.com/boot-clj/boot/tree/master/doc)
